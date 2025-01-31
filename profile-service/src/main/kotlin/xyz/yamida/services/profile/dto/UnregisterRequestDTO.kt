package xyz.yamida.services.profile.dto

data class UnregisterRequestDTO(
    val discordId: String,
    val gameNickname: String
)