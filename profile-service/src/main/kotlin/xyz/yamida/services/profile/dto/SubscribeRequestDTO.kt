package xyz.yamida.services.profile.dto

data class SubscribeRequestDTO(
    val discordId: String,
    val gameNickname: String,
    val days: Int
)