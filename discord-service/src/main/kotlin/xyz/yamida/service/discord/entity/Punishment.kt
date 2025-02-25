package xyz.yamida.service.discord.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "punishments")
data class Punishment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var discordId: String,

    @Column(name = "type", nullable = false)
    var type: String = "ban",
    @Column(nullable = false)
    var reason: String = "",

    @Column(nullable = false)
    var punishmentDate: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = true)
    val punishmentEndDate: LocalDateTime? = null
)