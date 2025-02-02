package xyz.yamida.service.discord.dto

import xyz.yamida.service.discord.dto.api.DataTransferObject

data class UnregisterRequestDTO(
    val discordId: String,
    val gameNickname: String
) : DataTransferObject()