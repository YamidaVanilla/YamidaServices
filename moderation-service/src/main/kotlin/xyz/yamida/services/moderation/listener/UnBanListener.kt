package xyz.yamida.services.moderation.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import xyz.yamida.services.moderation.dto.BanRequestDTO
import xyz.yamida.services.moderation.dto.api.DataTransferObject
import xyz.yamida.services.moderation.repository.UserRepository

@Component
class UnBanListener(
    val userRepository: UserRepository,
    val objectMapper: ObjectMapper
) {
    @KafkaListener(topics = ["unban-events"], groupId = "unban-moderation-group")
    fun handleRegistration(message: String) {
        try {
            val unbanDto = DataTransferObject.fromTransfer<BanRequestDTO>(objectMapper, message)

            val user = userRepository.findByGameNickname(unbanDto.gameName) ?: return

            user.isBanned = true
            user.banReason = null
            userRepository.save(user)
        } catch (ex: Exception) {
            println(ex.message)
        }
    }


}