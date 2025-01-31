package xyz.yamida.services.profile.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val discordId: String,

    @Column(nullable = false)
    val gameNickname: String,

    @Column(nullable = false)
    var subscribeDays: Int = 0,

    @Column(name = "warn_count", nullable = false)
    var warnCount: Int = 0,

    @Column(nullable = false)
    var isBanned: Boolean = false,

    @Column
    var isMuted: Boolean = false
)
