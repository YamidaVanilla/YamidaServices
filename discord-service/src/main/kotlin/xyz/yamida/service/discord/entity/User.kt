package xyz.yamida.service.discord.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var discordId: String,

    @Column(nullable = false)
    var gameNickname: String,

    @Column(name = "user_rank", nullable = false)
    var userRank: String = "Игрок",

    @Column(nullable = false)
    var subscribeDays: Int = 0,

    @Column(name = "warn_count", nullable = false)
    var warnCount: Int = 0
)