package xyz.yamida.services.moderation.entity

import jakarta.persistence.*
import java.time.LocalDateTime

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

    @Column(name = "warn_count", nullable = false)
    var warnCount: Int = 0,

    @Column(name = "is_muted", nullable = false)
    var isMuted: Boolean = false,

    @Column(name = "mute_until", nullable = true)
    var muteUntil: LocalDateTime? = null,

    @Column(name = "is_banned", nullable = false)
    var isBanned: Boolean = false,

    @Column(name = "ban_reason", nullable = true)
    var banReason: String? = null
)
