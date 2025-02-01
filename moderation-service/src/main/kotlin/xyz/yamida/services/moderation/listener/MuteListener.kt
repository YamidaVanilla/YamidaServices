package xyz.yamida.services.moderation.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xyz.yamida.services.moderation.controller.mute.MuteService
import xyz.yamida.services.moderation.dto.MuteRequestDTO
import xyz.yamida.services.moderation.dto.UnMuteRequestDTO

@Service
class MuteListener(
    val moderationService: MuteService,
    val objectMapper: ObjectMapper
) {
    @KafkaListener(topics = ["mute-events"], groupId = "moderation")
    fun listenMuteEvents(event: String) {
        val request = objectMapper.readValue(event, MuteRequestDTO::class.java)
        request.duration?.let { moderationService.muteUser(request.gameName, it) }
    }

    @KafkaListener(topics = ["unmute-events"], groupId = "moderation")
    fun listenUnmuteEvents(event: String) {
        val request = objectMapper.readValue(event, UnMuteRequestDTO::class.java)
        moderationService.unMuteUser(request.gameName)
    }
}
