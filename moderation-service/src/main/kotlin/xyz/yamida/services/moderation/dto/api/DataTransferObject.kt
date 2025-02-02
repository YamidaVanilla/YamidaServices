package xyz.yamida.services.moderation.dto.api

import com.fasterxml.jackson.databind.ObjectMapper

abstract class DataTransferObject {
    fun toTransfer(objectMapper: ObjectMapper): String {
        return objectMapper.writeValueAsString(this)
    }

    companion object {
        inline fun <reified T : DataTransferObject> fromTransfer(objectMapper: ObjectMapper, data: String): T {
            return objectMapper.readValue(data, T::class.java)
        }
    }
}
