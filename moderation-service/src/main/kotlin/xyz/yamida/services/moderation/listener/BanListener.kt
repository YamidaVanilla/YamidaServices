package xyz.yamida.services.moderation.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xyz.yamida.services.moderation.controller.ban.BanService
import xyz.yamida.services.moderation.dto.BanRequestDTO
import xyz.yamida.services.moderation.dto.UnBanRequestDTO

@Service
class BanListener(
    val moderationService: BanService,
    val objectMapper: ObjectMapper
) {
    @KafkaListener(topics = ["ban-events"], groupId = "moderation")
    fun listenBanEvents(event: String) {
        val request = objectMapper.readValue(event, BanRequestDTO::class.java)
        println("""
            Новый запрос на бан:
            ${request.gameName}
            ${request.reason}
        """.trimIndent())
        moderationService.banUser(request.gameName, request.reason)
    }

    @KafkaListener(topics = ["unban-events"], groupId = "moderation")
    fun listenUnbanEvents(event: String) {
        val request = objectMapper.readValue(event, UnBanRequestDTO::class.java)
        println("""
            Новый запрос на разбан:
            ${request.gameName}
        """.trimIndent())
        moderationService.unbanUser(request.gameName)
    }
}
