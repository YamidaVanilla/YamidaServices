package xyz.yamida.service.discord.dto

import xyz.yamida.service.discord.dto.api.DataTransferObject

data class BanRequestDTO(
    val gameName: String,
    val reason: String
) : DataTransferObject()