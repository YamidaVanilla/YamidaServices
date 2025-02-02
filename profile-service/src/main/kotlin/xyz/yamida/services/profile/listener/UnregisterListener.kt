package xyz.yamida.services.profile.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import xyz.yamida.services.profile.dto.UnregisterRequestDTO
import xyz.yamida.services.profile.dto.api.DataTransferObject
import xyz.yamida.services.profile.repository.UserRepository

@Component
class UnregisterListener(
    val userRepository: UserRepository,
    val objectMapper: ObjectMapper
) {
    @KafkaListener(topics = ["unregister-events"], groupId = "unregister-profile-group")
    fun handleUnregister(message: String) {
        try {
            val unregisterRequest = DataTransferObject.fromTransfer<UnregisterRequestDTO>(objectMapper, message)

            val existingUser = userRepository.findByDiscordIdOrGameNickname(
                unregisterRequest.discordId, unregisterRequest.gameNickname
            )

            println("""
                Пришел запрос на анрегестрацию:
                ${unregisterRequest.gameNickname}
                ${unregisterRequest.discordId}
            """.trimIndent())

            if (existingUser != null) {
                userRepository.delete(existingUser)
            }
        } catch (ex: Exception) {
            println("Failed to unregister ${ex.message}")
        }
    }
}