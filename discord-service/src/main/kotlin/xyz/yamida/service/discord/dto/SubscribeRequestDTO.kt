package xyz.yamida.service.discord.dto

data class SubscribeRequestDTO(
    val discordId: String,
    val gameNickname: String,
    val days: Int
)