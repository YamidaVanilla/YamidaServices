package xyz.yamida.service.discord.bot.commands

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.kafka.core.KafkaTemplate
import xyz.yamida.jda.commander.SlashCommand
import xyz.yamida.jda.commander.api.option.BaseCommandOptions
import xyz.yamida.jda.commander.api.option.ext.intParam
import xyz.yamida.jda.commander.api.option.ext.stringParam
import xyz.yamida.service.discord.dto.SubscribeRequestDTO
import xyz.yamida.service.discord.repository.UserRepository

class SubscriptionCommand(
    val userRepository: UserRepository,
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper
) : SlashCommand() {

    override val name: String = "subscription"
    override val description: String = "Управление подписками пользователей"

    inner class Options : BaseCommandOptions() {
        val identifier = stringParam {
            name = "id"
            description = "Уникальный идентификатор пользователя"
        }

        val days = intParam {
            name = "days"
            description = "Количество дней для подписки (отрицательное значение для снятия подписки)"
        }
    }

    override val options = Options()

    override fun hasPermission(event: SlashCommandInteractionEvent): Boolean {
        val member = event.member ?: return false
        return member.hasPermission(Permission.ADMINISTRATOR)
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        val identifier = options.identifier.get(event)!!
        val days = options.days.get(event)!!

        val user = userRepository.findByDiscordIdOrGameNickname(identifier) ?: run {
            event.reply("Пользователь с ID `$identifier` не найден.").setEphemeral(true).queue()
            return
        }

        if (days > 0) {
            user.subscribeDays += days
            event.reply("Пользователю `$identifier` добавлено $days дней подписки.").queue()
        } else {
            val updatedDays = (user.subscribeDays + days).coerceAtLeast(0)
            val removedDays = user.subscribeDays - updatedDays
            user.subscribeDays = updatedDays
            event.reply("У пользователя `$identifier` снято $removedDays дней подписки. Текущее количество дней: $updatedDays.").queue()
        }

        userRepository.save(user)

        val message = SubscribeRequestDTO(
            discordId = identifier,
            gameNickname = user.gameNickname,
            days = days
        )
        val messageJson = objectMapper.writeValueAsString(message)
        kafkaTemplate.send("subscribe-events", messageJson)
    }
}