package xyz.yamida.service.discord.dto

import xyz.yamida.service.discord.dto.api.DataTransferObject

data class BanRequestDTO(
    val gameName: String? = null,
    val discordId: String? = null,
    val reason: String
) : DataTransferObject() {
    override val topic: String = "ban-events"
}