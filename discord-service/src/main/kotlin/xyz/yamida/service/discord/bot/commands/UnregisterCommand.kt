package xyz.yamida.service.discord.bot.commands

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.kafka.core.KafkaTemplate
import xyz.yamida.jda.commander.SlashCommand
import xyz.yamida.jda.commander.api.option.BaseCommandOptions
import xyz.yamida.jda.commander.api.option.ext.stringParam
import xyz.yamida.service.discord.dto.UnregisterRequestDTO
import xyz.yamida.service.discord.repository.UserRepository

class UnregisterCommand(
    val userRepository: UserRepository,
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper
) : SlashCommand() {

    override val name: String = "unregister"
    override val description: String = "Удаляет профиль пользователя из базы данных."

    inner class Options : BaseCommandOptions() {
        val identifier = stringParam {
            name = "id"
            description = "Уникальный идентификатор пользователя."
        }
    }

    override val options = Options()

    override fun hasPermission(event: SlashCommandInteractionEvent): Boolean {
        val member = event.member ?: return false

        return member.hasPermission(Permission.ADMINISTRATOR)
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        val identifier = options.identifier.get(event) ?: return
        val user = userRepository.findByDiscordIdOrGameNickname(identifier)

        if (user == null) {
            event.reply("Пользователь с идентификатором`$identifier` не найден. Убедитесь, что он указан верно.").setEphemeral(true).queue()
            return
        }

        userRepository.delete(user)

        val request = UnregisterRequestDTO(
            user.discordId,
            user.gameNickname
        )

        kafkaTemplate.send("unregister-events", request.toTransfer(objectMapper))

        val embed = EmbedBuilder()
            .setTitle("Пользователь успешно удалён")
            .setDescription("Профиль с ником `${user.gameNickname} (${user.discordId})` был успешно удалён из базы данных.")
            .setColor(0xFF5555)
            .build()

        event.replyEmbeds(embed).queue()
    }
}