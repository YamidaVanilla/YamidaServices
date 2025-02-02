package xyz.yamida.service.discord.dto

import xyz.yamida.service.discord.dto.api.DataTransferObject

data class UnMuteRequestDTO(
    val gameName: String
) : DataTransferObject()