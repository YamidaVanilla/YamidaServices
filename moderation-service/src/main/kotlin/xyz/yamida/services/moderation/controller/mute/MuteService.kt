package xyz.yamida.services.moderation.controller.mute

import org.springframework.stereotype.Service
import xyz.yamida.services.moderation.repository.UserRepository
import java.time.LocalDateTime

@Service
class MuteService(val userRepository: UserRepository) {

    fun muteUser(gameName: String, duration: Long): String {
        val user = userRepository.findByGameNickname(gameName) ?: return "User not found"
        user.isMuted = true
        user.muteUntil = LocalDateTime.now().plusSeconds(duration)
        userRepository.save(user)
        return "User $gameName until ${user.muteUntil}"
    }

    fun unMuteUser(gameName: String): String {
        val user = userRepository.findByGameNickname(gameName) ?: return "User not found"
        user.isMuted = false
        user.muteUntil = null
        userRepository.save(user)
        return "User $gameName unmuted"
    }

    fun isMuted(gameName: String): Boolean {
        val user = userRepository.findByGameNickname(gameName) ?: return false
        return user.isMuted
    }
}