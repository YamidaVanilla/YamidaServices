package xyz.yamida.service.discord.dto

import xyz.yamida.service.discord.dto.api.DataTransferObject

data class UnBanRequestDTO(
    val gameName: String
) : DataTransferObject() {
    override val topic: String = "unban-events"
}