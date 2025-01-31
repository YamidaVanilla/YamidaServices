package xyz.yamida.services.profile.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.yamida.services.profile.entity.User

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByDiscordId(discordId: String): Boolean

    fun existsByGameNickname(nickname: String): Boolean

    fun findByDiscordId(discordId: String): User?

    fun findByGameNickname(gameNickname: String): User?

    fun findByDiscordIdOrGameNickname(discordId: String, gameNickname: String): User?
}
