package xyz.yamida.services.moderation.controller.ban

import org.springframework.stereotype.Service
import xyz.yamida.services.moderation.repository.UserRepository

@Service
class BanService(
    val userRepository: UserRepository
) {
    fun banUser(gameName: String, reason: String): String {
        val user = userRepository.findByGameNickname(gameName) ?: return "User not found"
        user.isBanned = true
        user.banReason = reason
        userRepository.save(user)
        return "User has been banned"
    }

    fun unbanUser(gameName: String): String {
        val user = userRepository.findByGameNickname(gameName) ?: return "User not found"
        user.isBanned = false
        user.banReason = null
        userRepository.save(user)
        return "User has been unbanned"
    }

    fun isBanned(gameName: String): String? {
        val user = userRepository.findByGameNickname(gameName) ?: return "User not found"
        return user.banReason
    }
}