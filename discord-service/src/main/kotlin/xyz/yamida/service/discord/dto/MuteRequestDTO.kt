package xyz.yamida.service.discord.dto

import xyz.yamida.service.discord.dto.api.DataTransferObject

data class MuteRequestDTO(
    val gameName: String,
    val duration: Long? = null
) : DataTransferObject()