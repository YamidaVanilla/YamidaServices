package xyz.yamida.services.moderation.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import xyz.yamida.services.moderation.dto.RegistrationRequestDTO
import xyz.yamida.services.moderation.entity.User
import xyz.yamida.services.moderation.repository.UserRepository

@Component
class UserManageListener(
    val userRepository: UserRepository,
    val objectMapper: ObjectMapper
) {
    @KafkaListener(topics = ["register-events"], groupId = "profile")
    fun handleRegistration(message: String) {
        try {
            val registrationDTO = objectMapper.readValue(message, RegistrationRequestDTO::class.java)

            val existingUser = userRepository.findByDiscordId(registrationDTO.discordId)
            if (existingUser != null) {
                return
            }

            val user = User(
                discordId = registrationDTO.discordId,
                gameNickname = registrationDTO.gameNickname
            )
            userRepository.save(user)

        } catch (ex: Exception) {
            println(ex.message)
        }
    }

}