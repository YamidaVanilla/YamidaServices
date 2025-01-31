package xyz.yamida.service.discord.controller.user

import org.springframework.stereotype.Service
import xyz.yamida.service.discord.repository.UserRepository

@Service
class UserService(val userRepository: UserRepository) {
    fun getDiscordIdByNickname(nickname: String): String {
        val user = userRepository.findByGameNickname(nickname) ?: return "User with nickname $nickname not found"
        return user.discordId
    }
}