package xyz.yamida.services.moderation.dto

import xyz.yamida.services.moderation.dto.api.DataTransferObject

data class UnBanRequestDTO(
    val gameName: String
) : DataTransferObject() {
    override val topic: String = "unban-events"
}