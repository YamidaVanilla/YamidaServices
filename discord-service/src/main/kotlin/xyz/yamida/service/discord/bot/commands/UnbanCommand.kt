package xyz.yamida.service.discord.bot.commands

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.kafka.core.KafkaTemplate
import xyz.yamida.jda.commander.SlashCommand
import xyz.yamida.jda.commander.api.option.BaseCommandOptions
import xyz.yamida.jda.commander.api.option.ext.stringParam
import xyz.yamida.service.discord.dto.UnBanRequestDTO
import xyz.yamida.service.discord.repository.PunishmentRepository
import xyz.yamida.service.discord.repository.UserRepository

class UnbanCommand(
    val userRepository: UserRepository,
    val punishmentRepository: PunishmentRepository,
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper
) : SlashCommand() {

    override val name: String = "unban"
    override val description: String = "Снять бан с пользователя"

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
            event.reply("Пользователя с идентификатором `$identifier` не найдено!").queue()
            return
        }

        val punishments = punishmentRepository.findByDiscordId(user.discordId)?.filter { it.type == "ban" }
        if (punishments.isNullOrEmpty()) {
            event.reply("У пользователя `${user.gameNickname} (${user.discordId})` нет активных банов.").queue()
            return
        }

        val request = UnBanRequestDTO(
            gameName = user.gameNickname
        )

        kafkaTemplate.send(request.topic, request.toTransfer(objectMapper))
        event.reply("Пользователь `${user.gameNickname} (${user.discordId})` разбанен.").queue()
        punishmentRepository.deleteAll(punishments)
        punishmentRepository.findByDiscordId(user.discordId)?.forEach {
            println(it)
        }
    }
}