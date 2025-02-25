package xyz.yamida.service.discord.dto

import xyz.yamida.service.discord.dto.api.DataTransferObject

data class SubscribeRequestDTO(
    val discordId: String,
    val gameNickname: String,
    val days: Int
) : DataTransferObject() {
    override val topic: String = "subscribe-events"
}