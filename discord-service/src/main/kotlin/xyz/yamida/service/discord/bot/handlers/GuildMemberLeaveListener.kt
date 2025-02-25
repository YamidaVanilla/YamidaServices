package xyz.yamida.service.discord.bot.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.events.guild.GuildBanEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.kafka.core.KafkaTemplate
import xyz.yamida.service.discord.dto.BanRequestDTO
import xyz.yamida.service.discord.dto.UnregisterRequestDTO
import xyz.yamida.service.discord.repository.UserRepository

class GuildMemberLeaveListener(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val userRepository: UserRepository
) : ListenerAdapter() {

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val userId = event.user.id
        val existingUser = userRepository.findByDiscordId(userId)

        if (existingUser != null) {
            userRepository.delete(existingUser)
            val unregisterRequest = UnregisterRequestDTO(
                discordId = userId,
                gameNickname = null,
            )
            kafkaTemplate.send("unregister-events", unregisterRequest.toTransfer(objectMapper))
        }
    }

    override fun onGuildBan(event: GuildBanEvent) {
        val userId = event.user.id
        val existingUser = userRepository.findByDiscordId(userId)

        if (existingUser != null) {
            userRepository.delete(existingUser)
            val banRequest = BanRequestDTO(
                discordId = userId,
                reason = "Ban by moderator"
            )
            kafkaTemplate.send("ban-events", banRequest.toTransfer(objectMapper))
        }
    }
}
