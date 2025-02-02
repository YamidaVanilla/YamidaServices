package xyz.yamida.services.profile.dto

import xyz.yamida.services.profile.dto.api.DataTransferObject

data class SubscribeRequestDTO(
    val discordId: String,
    val gameNickname: String,
    val days: Int
) : DataTransferObject()