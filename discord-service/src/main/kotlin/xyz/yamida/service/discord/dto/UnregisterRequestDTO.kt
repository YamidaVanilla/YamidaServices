package xyz.yamida.service.discord.dto

data class UnregisterRequestDTO(
    val discordId: String,
    val gameNickname: String
)