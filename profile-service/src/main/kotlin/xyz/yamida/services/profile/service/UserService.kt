package xyz.yamida.services.profile.service

import org.springframework.stereotype.Service
import xyz.yamida.services.profile.repository.UserRepository

@Service
class UserService(
    val userRepository: UserRepository
) {

    fun isAuthorizedByDiscordId(discordId: String): Boolean {
        return userRepository.existsByDiscordId(discordId)
    }

    fun isAuthorizedByNickname(nickname: String): Boolean {
        return userRepository.existsByGameNickname(nickname)
    }

    fun getSubscriptionDaysLeftByDiscordId(discordId: String): Int? {
        val user = userRepository.findByDiscordId(discordId)
        return user?.subscribeDays
    }

    fun getSubscriptionDaysLeftByNickname(nickname: String): Int? {
        val user = userRepository.findByGameNickname(nickname)
        return user?.subscribeDays
    }
}