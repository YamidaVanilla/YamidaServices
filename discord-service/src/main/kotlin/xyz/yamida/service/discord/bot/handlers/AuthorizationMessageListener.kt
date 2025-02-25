package xyz.yamida.service.discord.bot.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import xyz.yamida.service.discord.bot.storager.RequestStorage
import xyz.yamida.service.discord.dto.RegistrationRequestDTO
import xyz.yamida.service.discord.entity.User
import xyz.yamida.service.discord.repository.PunishmentRepository
import xyz.yamida.service.discord.repository.UserRepository
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@Component
class AuthorizationMessageListener(
    val userRepository: UserRepository,
    val punishmentRepository: PunishmentRepository,
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper
) : ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.componentId != "authmenu_button") return

        val punishments = punishmentRepository.findByDiscordId(event.user.id) ?: emptyList()
        punishments.forEach {
            println(it)
        }
        if (punishments.any { it.type == "ban" }) {
            event.replyEmbeds(createBanEmbed(punishments.first { it.type == "ban" }.reason)).setEphemeral(true).queue()
            return
        }
        if (RequestStorage.isRequested(event.user.id)) {
            event.reply("Ваша заявка уже отправлена на проверку. Ожидайте решения администрации.").setEphemeral(true).queue()
            return
        }

        val existingUser = userRepository.findByDiscordId(event.user.id)
        if (existingUser != null) {
            event.replyEmbeds(createAlreadyRegisteredEmbed(existingUser.gameNickname)).setEphemeral(true).queue()
            return
        }

        event.replyModal(createRegistrationModal()).queue()
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        if (event.modalId != "auth_modal") return

        val nickname = event.getValue("nickname")?.asString ?: "Не указано"
        val daysSinceRegistration = ChronoUnit.DAYS.between(event.user.timeCreated, OffsetDateTime.now())

        if (daysSinceRegistration < 5) {
            sendVerificationRequest(event, nickname)
            RequestStorage.addRequest(event.user.id, nickname)
            event.reply("Ваша заявка отправлена на проверку. Ожидайте решения администрации.").setEphemeral(true).queue()
        } else {
            handleUserRegistration(event, nickname)
        }
    }

    fun sendVerificationRequest(event: ModalInteractionEvent, nickname: String) {
        val verificationChannel = event.jda.getTextChannelById("1294968774319145000") ?: return

        val embed = EmbedBuilder()
            .setTitle("Новая заявка на регистрацию")
            .setDescription("Пользователь: ${event.user.asMention}\nДискорд ID: `${event.user.id}`\nИгровой ник: `$nickname` \nДата регистрации: ${event.user.timeCreated}")
            .setColor(0xFFA500)
            .setFooter("Зарегистрирован менее 5 дней назад")
            .build()

        verificationChannel.sendMessageEmbeds(embed)
            .setActionRow(
                Button.danger("ban_user:${event.user.id}", "🚨 Забанить"),
                Button.success("accept_user:${event.user.id}", "✅ Принять заявку"),
                Button.secondary("reject_user:${event.user.id}", "❌ Отклонить")
            ).queue()
    }

    fun handleUserRegistration(event: ModalInteractionEvent, nickname: String) {
        try {
            val user = userRepository.save(User(discordId = event.user.id, gameNickname = nickname))
            val request = RegistrationRequestDTO(user.discordId, user.gameNickname)
            kafkaTemplate.send(request.topic, request.toTransfer(objectMapper))
            event.user.openPrivateChannel().queue { privateChannel ->
                privateChannel.sendMessageEmbeds(EmbedBuilder()
                    .setTitle("Регистрация завершена")
                    .setDescription("Вы зарегистрированы. Ваш ник: `$nickname`\n**IP**: `mc.yamida.xyz` **Версия**: `1.21.3`")
                    .setColor(0xF695CB)
                    .build()).queue()
            }
            event.reply("Вы успешно зарегистрированы.").setEphemeral(true).queue()
        } catch (e: Exception) {
            event.reply("Ошибка регистрации: ${e.message}").setEphemeral(true).queue()
        }
    }

    override fun onReady(event: ReadyEvent) {
        event.jda.getTextChannelById(1327400152545099849)?.sendMessageEmbeds(
            EmbedBuilder()
                .setTitle("Как попасть на сервер:")
                .setDescription(
                    """
                Чтобы начать играть на сервере, нажмите на кнопку ниже и введите ваш игровой ник в Minecraft.
                После этого ваш Discord аккаунт будет привязан, и вы сможете играть на сервере.

                **Важно!**
                Вы больше не сможете самостоятельно отвязать этот **Discord** аккаунт от **Minecraft** аккаунта. В случае ошибки обратитесь к модерации сервера.
                """.trimIndent()
                )
                .setColor(0xF695CB)
                .build()
        )?.setActionRow(Button.primary("authmenu_button", "✍️ Открыть меню"))?.queue()
    }

    fun createBanEmbed(reason: String) = EmbedBuilder()
        .setTitle("Аккаунт заблокирован")
        .setDescription("Причина: `$reason`")
        .setColor(0xFF0000)
        .build()

    fun createAlreadyRegisteredEmbed(nickname: String) = EmbedBuilder()
        .setTitle("Аккаунт")
        .setDescription("Вы уже зарегистрированы. Ваш ник: `$nickname`")
        .setColor(0xF695CB)
        .build()

    fun createRegistrationModal() = Modal.create("auth_modal", "Регистрация")
        .addActionRow(
            TextInput.create("nickname", "Ваш никнейм", TextInputStyle.SHORT)
                .setPlaceholder("Введите никнейм")
                .setRequired(true)
                .build()
        ).build()
}