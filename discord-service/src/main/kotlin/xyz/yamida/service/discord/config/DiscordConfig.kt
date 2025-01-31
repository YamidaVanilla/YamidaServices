package xyz.yamida.service.discord.config

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import xyz.yamida.jda.commander.CommandManager
import xyz.yamida.service.discord.bot.commands.ProfileCommand
import xyz.yamida.service.discord.bot.commands.SubscriptionCommand
import xyz.yamida.service.discord.bot.commands.UnregisterCommand
import xyz.yamida.service.discord.bot.handlers.AuthorizationMessageListener
import xyz.yamida.service.discord.bot.handlers.GuildMemberLeaveListener
import xyz.yamida.service.discord.controller.punishments.PunishmentService
import xyz.yamida.service.discord.repository.UserRepository

@Configuration
class DiscordConfig(
    @Value("\${discord.bot.token}") val token: String
) {

    @Bean
    fun jda(): JDA {
        return JDABuilder
            .createLight(
                token,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS
            ).build()
    }

    @Bean
    fun commandManager(
        jda: JDA,
        userRepository: UserRepository,
        punishmentService: PunishmentService,
        kafkaTemplate: KafkaTemplate<String, String>,
        objectMapper: ObjectMapper
    ): CommandManager {
        val commands = listOf(
            ProfileCommand(userRepository),
            UnregisterCommand(userRepository, kafkaTemplate, objectMapper),
            SubscriptionCommand(userRepository, kafkaTemplate, objectMapper),
        )
        val commandManager = CommandManager(commands)
        jda.addEventListener(
            AuthorizationMessageListener(userRepository, kafkaTemplate, objectMapper),
            GuildMemberLeaveListener(kafkaTemplate, objectMapper, userRepository),
            commandManager
        )
        println("Registering commands")

        return commandManager
    }

    @Bean
    fun setPresence(jda: JDA): Boolean {
        println("Setting presence")
        jda.presence.setPresence(
            Activity.watching("за сервером"),
            false
        )
        return true
    }
}