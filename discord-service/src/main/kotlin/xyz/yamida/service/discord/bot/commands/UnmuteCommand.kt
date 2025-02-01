package xyz.yamida.service.discord.bot.commands

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.kafka.core.KafkaTemplate
import xyz.yamida.jda.commander.SlashCommand
import xyz.yamida.jda.commander.api.option.BaseCommandOptions
import xyz.yamida.jda.commander.api.option.ext.stringParam
import xyz.yamida.service.discord.dto.UnMuteRequestDTO
import xyz.yamida.service.discord.repository.UserRepository

class UnmuteCommand(
    val userRepository: UserRepository,
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper
) : SlashCommand() {

    override val name: String = "unmute"
    override val description: String = "Снять мут с пользователя"

    inner class Options : BaseCommandOptions() {
        val identifier = stringParam {
            name = "id"
            description = "Уникальный идентификатор пользователя"
        }
    }

    override val options = Options()

    override fun hasPermission(event: SlashCommandInteractionEvent): Boolean {
        val member = event.member ?: return false
        return member.hasPermission(Permission.ADMINISTRATOR)
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        val identifier = options.identifier.get(event)!!

        val user = userRepository.findByDiscordIdOrGameNickname(identifier) ?: run {
            event.reply("Пользователя с идентификатором `$identifier` не найдено!")
            return
        }

        val request = UnMuteRequestDTO(
            gameName = user.gameNickname
        )
        val messageJson = objectMapper.writeValueAsString(request)
        kafkaTemplate.send("unmute-events", messageJson)

        event.reply("Пользователь `$identifier` размучен.").queue()
    }
}