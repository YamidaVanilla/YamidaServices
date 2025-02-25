package xyz.yamida.services.moderation.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import xyz.yamida.services.moderation.dto.BanRequestDTO
import xyz.yamida.services.moderation.dto.api.DataTransferObject
import xyz.yamida.services.moderation.repository.UserRepository

@Component
class BanListener(
    val userRepository: UserRepository,
    val objectMapper: ObjectMapper
) {
    @KafkaListener(topics = ["ban-events"], groupId = "ban-moderation-group")
    fun handleRegistration(message: String) {
        try {
            val banDto = DataTransferObject.fromTransfer<BanRequestDTO>(objectMapper, message)

            val user = userRepository.findByGameNicknameOrDiscordId(banDto.gameName, banDto.discordId) ?: return
            user.apply {
                isBanned = true
                banReason = banDto.reason
            }
            userRepository.save(user)
        } catch (ex: Exception) {
            println(ex.message)
        }
    }


}