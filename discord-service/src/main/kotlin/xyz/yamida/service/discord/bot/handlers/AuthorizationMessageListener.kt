package xyz.yamida.service.discord.bot.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
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
import xyz.yamida.service.discord.dto.RegistrationRequestDTO
import xyz.yamida.service.discord.entity.User
import xyz.yamida.service.discord.repository.UserRepository

@Component
class AuthorizationMessageListener(
    val userRepository: UserRepository,
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper
) : ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.componentId == "authmenu_button") {
            val existingUser = userRepository.findByDiscordId(event.user.id)

            if (existingUser != null) {
                val embed = EmbedBuilder()
                    .setTitle("Аккаунт")
                    .setDescription("Вы уже зарегистрированы на сервере. Ваш игровой ник: `${existingUser.gameNickname}`")
                    .setColor(0xF695CB)
                    .build()

                event.replyEmbeds(embed).setEphemeral(true).queue()
                return
            }

            val nicknameInput = TextInput.create("nickname", "Ваш никнейм в игре", TextInputStyle.SHORT)
                .setPlaceholder("Введите ваш никнейм")
                .setRequired(true)
                .build()

            val modal = Modal.create("auth_modal", "Регистрация на проекте")
                .addActionRow(nicknameInput)
                .build()

            event.replyModal(modal).queue()
        }
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        if (event.modalId == "auth_modal") {
            val nickname = event.getValue("nickname")?.asString ?: "Не указано"

            event.user.openPrivateChannel().queue({ privateChannel ->
                val embed = createRegistrationSuccessEmbed(nickname)
                privateChannel.sendMessageEmbeds(embed).queue({
                    handleUserRegistration(event, nickname)
                }, {
                    event.reply("Ошибка: Не удалось отправить личное сообщение. Проверьте настройки личных сообщений пользователя.").setEphemeral(true).queue()
                })
            }, {
                event.reply("Ошибка: Не удалось открыть личный канал пользователя.").setEphemeral(true).queue()
            })
        }
    }

    fun createRegistrationSuccessEmbed(nickname: String): MessageEmbed {
        return EmbedBuilder()
            .setTitle("Регистрация завершена")
            .setDescription(
                """
                Вы успешно прошли регистрацию. Ваш игровой ник: `$nickname`
                **IP**: `mc.yamida.xyz` **Версия**: `1.21.3`
                Спасибо, что выбираете нас!
                """.trimIndent()
            )
            .setColor(0xF695CB)
            .build()
    }

    fun handleUserRegistration(event: ModalInteractionEvent, nickname: String) {
        val registrationDTO = RegistrationRequestDTO(
            discordId = event.user.id,
            gameNickname = nickname
        )

        val user = User(
            discordId = registrationDTO.discordId,
            gameNickname = registrationDTO.gameNickname
        )

        try {
            userRepository.save(user)
            kafkaTemplate.send("register_topic", objectMapper.writeValueAsString(registrationDTO))
            event.reply("Личное сообщение с информацией о регистрации отправлено.").setEphemeral(true).queue()
        } catch (e: Exception) {
            event.reply("Ошибка: Не удалось зарегистрировать пользователя. ${e.message}").setEphemeral(true).queue()
        }
    }

    override fun onReady(event: ReadyEvent) {
        val channel = event.jda.getTextChannelById(1327400152545099849)

        val embed = EmbedBuilder()
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

        channel?.sendMessageEmbeds(embed)
            ?.setActionRow(Button.primary("authmenu_button", "✍️ Открыть меню"))
            ?.queue()
    }
}