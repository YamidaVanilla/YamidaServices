package xyz.yamida.service.discord.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import xyz.yamida.service.discord.entity.User

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByDiscordId(discordId: String): User?

    fun findByGameNickname(gameNickname: String): User?

    @Query("SELECT u FROM User u WHERE u.discordId = :identifier OR u.gameNickname = :identifier")
    fun findByDiscordIdOrGameNickname(@Param("identifier") identifier: String): User?
}
