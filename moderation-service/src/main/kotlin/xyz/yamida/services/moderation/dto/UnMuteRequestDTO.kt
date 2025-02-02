package xyz.yamida.services.moderation.dto

import xyz.yamida.services.moderation.dto.api.DataTransferObject

data class UnMuteRequestDTO(
    val gameName: String
) : DataTransferObject()