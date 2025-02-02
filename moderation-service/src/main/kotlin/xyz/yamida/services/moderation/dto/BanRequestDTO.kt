package xyz.yamida.services.moderation.dto

import xyz.yamida.services.moderation.dto.api.DataTransferObject

data class BanRequestDTO(
    val gameName: String,
    val reason: String
) : DataTransferObject()