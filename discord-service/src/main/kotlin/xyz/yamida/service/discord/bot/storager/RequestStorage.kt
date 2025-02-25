package xyz.yamida.service.discord.bot.storager

object RequestStorage {
    val requested = hashMapOf<String, String>()

    fun addRequest(discordId: String, gameName: String) {
        requested[discordId] = gameName
    }

    fun removeRequest(discordId: String) {
        requested.remove(discordId)
    }

    fun isRequested(discordId: String): Boolean {
        return requested.contains(discordId)
    }

    fun getGameName(discordId: String): String? {
        return requested[discordId]
    }
}