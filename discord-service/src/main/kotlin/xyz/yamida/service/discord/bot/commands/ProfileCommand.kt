package xyz.yamida.service.discord.bot.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import xyz.yamida.jda.commander.SlashCommand
import xyz.yamida.jda.commander.api.option.BaseCommandOptions
import xyz.yamida.jda.commander.api.option.ext.stringParam
import xyz.yamida.service.discord.repository.PunishmentRepository
import xyz.yamida.service.discord.repository.UserRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ProfileCommand(
    private val userRepository: UserRepository,
    private val punishmentRepository: PunishmentRepository
) : SlashCommand() {
    override val name: String = "profile"
    override val description: String = "Отображает информацию профиля игрока."

    inner class Options : BaseCommandOptions() {
        val identifier = stringParam {
            name = "id"
            description = "Уникальный идентификатор игрока."
            optional = true
        }
    }

    override val options = Options()

    override fun execute(event: SlashCommandInteractionEvent) {
        val identifier = options.identifier.get(event)?.let { extractIdFromMention(it) }
        val player = if (identifier != null) {
            userRepository.findByDiscordIdOrGameNickname(identifier)
        } else {
            userRepository.findByDiscordIdOrGameNickname(event.user.id)
        }


        if (player == null) {
            event.reply("Профиль игрока не найден. Убедитесь, что вы ввели корректный идентификатор.").queue()
            return
        }

        val subscriptionText = if (player.subscribeDays > 0) {
            val subscriptionEndDate = LocalDate.now().plusDays(player.subscribeDays.toLong() + 1)
            val formattedDate = subscriptionEndDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru")))
            "**Активна до: $formattedDate**"
        } else {
            "`Нет активной подписки`"
        }

        val ban = punishmentRepository.findByDiscordId(player.discordId)?.firstOrNull { it.type == "ban" }

        val embed = EmbedBuilder()
            .setTitle("Профиль игрока")
            .setDescription(
                """
                **Minecraft ник**: `${player.gameNickname}`
                **Discord ник**: <@${player.discordId}>
                **ID**: `${player.discordId}`

                **Ранг**: `${player.userRank}`

                **Premium подписка**: **${if (player.subscribeDays > 0) ":white_check_mark:" else ":x:"}**
                $subscriptionText

                **Количество предупреждений**: `${player.warnCount}`

                **Забанен**: ${if (ban == null) ":white_check_mark:" else ":x:"}
                **Причина**: `${ban?.reason ?: "Отсутствует"}`
                """.trimIndent()
            )
            .setColor(0x1ABC9C)
            .build()

        event.replyEmbeds(embed).queue()
    }

    fun extractIdFromMention(identifier: String): String {
        return identifier.removePrefix("<@").removeSuffix(">")
    }
}