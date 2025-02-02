package xyz.yamida.services.moderation.dto

import xyz.yamida.services.moderation.dto.api.DataTransferObject

data class MuteRequestDTO(
    val gameName: String,
    val duration: Long? = null
) : DataTransferObject()