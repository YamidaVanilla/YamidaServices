package xyz.yamida.service.discord.bot.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import xyz.yamida.service.discord.bot.storager.RequestStorage
import xyz.yamida.service.discord.dto.BanRequestDTO
import xyz.yamida.service.discord.dto.RegistrationRequestDTO
import xyz.yamida.service.discord.entity.Punishment
import xyz.yamida.service.discord.entity.User
import xyz.yamida.service.discord.repository.PunishmentRepository
import xyz.yamida.service.discord.repository.UserRepository

@Component
class VerificationHandler(
    val jda: JDA,
    val userRepository: UserRepository,
    val punishmentRepository: PunishmentRepository,
    val objectMapper: ObjectMapper,
    val kafkaTemplate: KafkaTemplate<String, String>
) : ListenerAdapter() {

    val log: Logger = LoggerFactory.getLogger(VerificationHandler::class.java)

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val parts = event.componentId.split(":")
        if (parts.size != 2) {
            return
        }

        val action = parts[0]
        val userId = parts[1]

        when (action) {
            "ban_user" -> {
                event.reply("Пользователь <@$userId> забанен.").queue()
                val request = BanRequestDTO(
                    discordId = userId,
                    reason = "Бан заявкой ${event.user.id}",
                )
                kafkaTemplate.send(request.topic, request.toTransfer(objectMapper))
                punishmentRepository.save(
                    Punishment(
                        discordId = userId,
                        reason = "Бан заявкой ${event.user.id}"
                    )
                )
                userRepository.save(
                    User(
                        discordId = userId,
                        gameNickname = RequestStorage.getGameName(userId) ?: "Не указано"
                    )
                )
                RequestStorage.removeRequest(userId)
                log.info("Пользователь $userId забанен модератором ${event.user.id}")
            }
            "accept_user" -> {
                if (!RequestStorage.isRequested(userId)) {
                    log.error("Пользователь $userId не отправлял заявку")
                    return
                }
                val request = RegistrationRequestDTO(
                    discordId = userId,
                    gameNickname = RequestStorage.getGameName(userId) ?: "Не указано",
                )
                kafkaTemplate.send(request.topic, request.toTransfer(objectMapper))
                userRepository.save(
                    User(
                        discordId = userId,
                        gameNickname = RequestStorage.getGameName(userId) ?: "Не указано"
                    )
                )
                jda.retrieveUserById(userId).complete().openPrivateChannel().complete().sendMessageEmbeds(
                    EmbedBuilder()
                        .setTitle("Регистрация завершена")
                        .setDescription("Вы зарегистрированы. \n**IP**: `mc.yamida.xyz` **Версия**: `1.21.3`")
                        .setColor(0xF695CB)
                        .build()).queue()
                event.reply("Пользователь <@$userId> принят на сервер.").queue()
                log.info("Пользователь $userId принят на сервер модератором ${event.user.id}")

            }
            "reject_user" -> {
                event.reply("Заявка пользователя <@$userId> отклонена.").queue()
                RequestStorage.removeRequest(userId)
                log.info("Заявка пользователя $userId отклонена модератором ${event.user.id}")
            }
        }
    }
}
