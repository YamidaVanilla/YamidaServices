package xyz.yamida.services.moderation.repository

import org.springframework.data.jpa.repository.JpaRepository
import xyz.yamida.services.moderation.entity.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByDiscordId(discordId: String): User?
    fun findByGameNickname(gameNickname: String): User?
}
