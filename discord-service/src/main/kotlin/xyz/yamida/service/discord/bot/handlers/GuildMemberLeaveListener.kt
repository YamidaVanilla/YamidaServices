package xyz.yamida.service.discord.bot.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.kafka.core.KafkaTemplate
import xyz.yamida.service.discord.dto.UnregisterRequestDTO
import xyz.yamida.service.discord.repository.UserRepository

class GuildMemberLeaveListener(
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper,
    val userRepository: UserRepository
) : ListenerAdapter() {

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val userId = event.user.id
        val existingUser = userRepository.findByDiscordId(userId)

        if (existingUser != null) {
            userRepository.delete(existingUser)

            val unregisterRequestDTO = UnregisterRequestDTO(
                existingUser.discordId,
                existingUser.gameNickname
            )
            val message = objectMapper.writeValueAsString(unregisterRequestDTO)
            kafkaTemplate.send("unregister_topic", message)
        }
    }
}