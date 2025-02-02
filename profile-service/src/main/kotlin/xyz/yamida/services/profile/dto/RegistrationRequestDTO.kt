package xyz.yamida.services.profile.dto

import xyz.yamida.services.profile.dto.api.DataTransferObject

data class RegistrationRequestDTO(
    val discordId: String,
    val gameNickname: String
) : DataTransferObject()