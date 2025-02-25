package xyz.yamida.service.discord.dto

import xyz.yamida.service.discord.dto.api.DataTransferObject

data class RegistrationRequestDTO(
    val discordId: String,
    val gameNickname: String
) : DataTransferObject() {
    override val topic: String = "register-events"
}