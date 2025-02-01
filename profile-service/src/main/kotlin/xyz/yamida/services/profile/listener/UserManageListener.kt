package xyz.yamida.services.profile.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import xyz.yamida.services.profile.dto.RegistrationRequestDTO
import xyz.yamida.services.profile.dto.UnregisterRequestDTO
import xyz.yamida.services.profile.entity.User
import xyz.yamida.services.profile.repository.UserRepository

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

    @KafkaListener(topics = ["unregister-events"], groupId = "profile")
    fun handleUnregister(message: String) {
        try {
            val unregisterRequest = objectMapper.readValue(message, UnregisterRequestDTO::class.java)

            val existingUser = userRepository.findByDiscordIdOrGameNickname(
                unregisterRequest.discordId, unregisterRequest.gameNickname
            )

            if (existingUser != null) {
                userRepository.delete(existingUser)
            }
        } catch (ex: Exception) {
            println("Failed to unregister ${ex.message}")
        }
    }


}