package xyz.yamida.services.profile.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import xyz.yamida.services.profile.dto.RegistrationRequestDTO
import xyz.yamida.services.profile.dto.api.DataTransferObject
import xyz.yamida.services.profile.entity.User
import xyz.yamida.services.profile.repository.UserRepository

@Component
class RegisterListener(
    val userRepository: UserRepository,
    val objectMapper: ObjectMapper
) {
    @KafkaListener(topics = ["register-events"], groupId = "register-profile-group")
    fun handleRegistration(message: String) {
        try {
            val registrationDTO = DataTransferObject.fromTransfer<RegistrationRequestDTO>(objectMapper, message)

            val existingUser = userRepository.findByDiscordId(registrationDTO.discordId)
            if (existingUser != null) {
                return
            }

            println("""
                Пришел запрос на регистрацию
                ${registrationDTO.gameNickname}
                ${registrationDTO.discordId}
            """.trimIndent())
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