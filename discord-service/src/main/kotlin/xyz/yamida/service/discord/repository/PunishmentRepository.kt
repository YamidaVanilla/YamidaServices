package xyz.yamida.service.discord.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import xyz.yamida.service.discord.entity.Punishment

@Repository
interface PunishmentRepository : JpaRepository<Punishment, Long> {
    fun findByDiscordId(discordId: String): List<Punishment>?

    @Query("SELECT p FROM Punishment p WHERE p.discordId = :identifier")
    fun findByDiscordIdOrGameNickname(@Param("identifier") identifier: String): List<Punishment>?
}
