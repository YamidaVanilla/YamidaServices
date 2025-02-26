package xyz.yamida.service.discord.bot.commands

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.kafka.core.KafkaTemplate
import xyz.yamida.jda.commander.SlashCommand
import xyz.yamida.jda.commander.api.option.BaseCommandOptions
import xyz.yamida.jda.commander.api.option.ext.intParam
import xyz.yamida.jda.commander.api.option.ext.stringParam
import xyz.yamida.service.discord.dto.MuteRequestDTO
import xyz.yamida.service.discord.repository.UserRepository
import xyz.yamida.service.discord.services.messaging.MessagingService

class MuteCommand(
    val userRepository: UserRepository,
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper,
    val messageService: MessagingService
) : SlashCommand() {

    override val name: String = "mute"
    override val description: String = "Выдать мут пользователю"

    inner class Options : BaseCommandOptions() {
        val identifier = stringParam {
            name = "id"
            description = "Уникальный идентификатор пользователя"
        }

        val duration = intParam {
            name = "duration"
            description = "Длительность мута в секундах"
        }
    }

    override val options = Options()

    override fun hasPermission(event: SlashCommandInteractionEvent): Boolean {
        val member = event.member ?: return false
        return member.hasPermission(Permission.ADMINISTRATOR)
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        val identifier = options.identifier.get(event)!!
        val duration = options.duration.get(event)!!

        val user = userRepository.findByDiscordIdOrGameNickname(identifier) ?: run {
            event.reply("Пользователя с идентификатором `$identifier` не найдено!").queue()
            return
        }

        val request = MuteRequestDTO(
            gameName = user.gameNickname,
            duration = duration.toLong()
        )
        val embed = EmbedBuilder()
            .setTitle("Вы получили наказание")
            .setDescription("Вы получили мут на игровом. Мут выдан на `$duration` секунд")
            .setColor(0xF695CB)
            .build()

        messageService.sendDirectEmbed(
            user.discordId,
            embed = embed,
        )
        kafkaTemplate.send(request.topic, request.toTransfer(objectMapper))

        event.reply("Пользователь `${user.gameNickname} (${user.discordId})` замучен на $duration секунд.").queue()
    }
}
