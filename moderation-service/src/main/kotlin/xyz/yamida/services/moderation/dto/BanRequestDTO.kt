package xyz.yamida.services.moderation.dto

data class BanRequestDTO(
    val gameName: String,
    val reason: String
)