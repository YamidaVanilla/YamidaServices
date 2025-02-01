package xyz.yamida.service.discord.bot.commands

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.kafka.core.KafkaTemplate
import xyz.yamida.jda.commander.SlashCommand
import xyz.yamida.jda.commander.api.option.BaseCommandOptions
import xyz.yamida.jda.commander.api.option.ext.stringParam
import xyz.yamida.service.discord.services.messaging.MessagingService
import xyz.yamida.service.discord.dto.BanRequestDTO
import xyz.yamida.service.discord.repository.UserRepository

class BanCommand(
    val userRepository: UserRepository,
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper,
    val messageService: MessagingService
) : SlashCommand() {

    override val name: String = "ban"
    override val description: String = "Выдать бан пользователю"

    inner class Options : BaseCommandOptions() {
        val identifier = stringParam {
            name = "id"
            description = "Уникальный идентификатор пользователя"
        }

        val reason = stringParam {
            name = "reason"
            description = "Причина бана"
        }
    }

    override val options = Options()

    override fun hasPermission(event: SlashCommandInteractionEvent): Boolean {
        val member = event.member ?: return false
        return member.hasPermission(Permission.ADMINISTRATOR)
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        val identifier = options.identifier.get(event)!!
        val reason = options.reason.get(event)!!

        val user = userRepository.findByDiscordIdOrGameNickname(identifier) ?: run {
            event.reply("Пользователя с идентификатором `$identifier` не найдено!")
            return
        }

        val request = BanRequestDTO(
            gameName = user.gameNickname,
            reason = reason
        )
        val embed = EmbedBuilder()
            .setTitle("Вы получили наказание")
            .setDescription("Вы получили бан на игровом сервере за `$reason`")
            .setColor(0xF695CB)
            .build()
        messageService.sendDirectEmbed(
            user.discordId,
            embed = embed,
        )
        val messageJson = objectMapper.writeValueAsString(request)
        kafkaTemplate.send("ban-events", messageJson)
        event.reply("Пользователь `$identifier` забанен с причиной `$reason`").queue()
    }
}
