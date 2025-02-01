package xyz.yamida.services.moderation.dto

data class MuteRequestDTO(
    val gameName: String,
    val duration: Long? = null
)