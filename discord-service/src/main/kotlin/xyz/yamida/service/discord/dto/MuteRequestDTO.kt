package xyz.yamida.service.discord.dto

data class MuteRequestDTO(
    val gameName: String,
    val duration: Long? = null
)