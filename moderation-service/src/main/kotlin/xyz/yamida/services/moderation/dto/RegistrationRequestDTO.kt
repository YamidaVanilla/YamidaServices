package xyz.yamida.services.moderation.dto

import xyz.yamida.services.moderation.dto.api.DataTransferObject

data class RegistrationRequestDTO(
    val discordId: String,
    val gameNickname: String
) : DataTransferObject()