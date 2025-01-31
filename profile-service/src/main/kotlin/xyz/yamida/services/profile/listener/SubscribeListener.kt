package xyz.yamida.services.profile.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import xyz.yamida.services.profile.dto.SubscribeRequestDTO
import xyz.yamida.services.profile.repository.UserRepository

@Component
class SubscribeListener(
    val userRepository: UserRepository,
    val objectMapper: ObjectMapper
) {
    @KafkaListener(topics = ["subscribe_topic"], groupId = "subscribe_group")
    fun handleGive(message: String) = try {
        val requestDTO = objectMapper.readValue(message, SubscribeRequestDTO::class.java)

        val user = userRepository.findByDiscordIdOrGameNickname(requestDTO.discordId, requestDTO.gameNickname)
            ?: throw Exception("Пользователь с ID '${requestDTO.discordId}' или ником '${requestDTO.gameNickname}' не найден.")

        if (requestDTO.days > 0) {
            user.subscribeDays += requestDTO.days
        } else {
            user.subscribeDays = maxOf(0, user.subscribeDays + requestDTO.days)
        }

        userRepository.save(user)

        println("Подписка пользователя '${user.discordId}' успешно обновлена. Текущее количество дней: ${user.subscribeDays}")
    } catch (ex: Exception) {
        println("Ошибка обработки подписки: ${ex.message}")
    }
}
