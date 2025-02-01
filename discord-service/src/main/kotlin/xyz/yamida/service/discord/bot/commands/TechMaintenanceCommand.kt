package xyz.yamida.service.discord.bot.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import xyz.yamida.jda.commander.SlashCommand
import xyz.yamida.jda.commander.api.option.BaseCommandOptions
import xyz.yamida.jda.commander.api.option.ext.stringParam
import xyz.yamida.service.discord.config.TechMaintenanceConfig
import java.awt.Color

class TechMaintenanceCommand(
    private val config: TechMaintenanceConfig
) : SlashCommand() {
    override val name: String = "maintenance"
    override val description: String = "Изменение статуса технических работ"

    inner class Options : BaseCommandOptions() {
        val newStatus = stringParam {
            name = "status"
            description = "Новый статус"
        }
    }

    override val options = Options()

    override fun hasPermission(event: SlashCommandInteractionEvent): Boolean {
        val member = event.member ?: return false
        return member.hasPermission(Permission.ADMINISTRATOR)
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        val newStatus = options.newStatus.get(event)
        val embed = EmbedBuilder()
            .setTitle("Статус технических работ")
            .setColor(if (newStatus == "on") Color.RED else Color.GREEN)

        if (newStatus == "on") {
            if (config.isEnabled()) {
                embed.setDescription("⚠️ Технические работы уже включены!")
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
            config.enable()
            embed.setDescription("✅ Технические работы **включены**")
        } else {
            if (!config.isEnabled()) {
                embed.setDescription("⚠️ Технические работы уже выключены!")
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
            config.disable()
            embed.setDescription("✅ Технические работы **выключены**")
        }
        event.replyEmbeds(embed.build()).queue()
    }
}
