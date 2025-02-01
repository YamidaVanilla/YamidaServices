package xyz.yamida.service.discord.services.messaging

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import org.springframework.stereotype.Service

@Service
class MessagingService(
    val jda: JDA
) {

    fun sendDirectMessage(userId: String, message: String): String {
        return try {
            jda.retrieveUserById(userId).queue { user ->
                user.openPrivateChannel().queue { channel ->
                    channel.sendMessage(message).queue()
                }
            }
            "Message request sent to user with ID: $userId"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun sendChannelMessage(channelId: String, message: String): String {
        val channel = jda.getTextChannelById(channelId)
            ?: return "Channel with ID $channelId not found"
        return try {
            channel.sendMessage(message).submit().get()
            "Message sent to channel with ID: $channelId"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun sendDirectEmbed(userId: String, embed: MessageEmbed): String {
        return try {
            jda.retrieveUserById(userId).queue({ user ->
                user.openPrivateChannel().queue({ channel ->
                    channel.sendMessageEmbeds(embed).queue()
                })
            })
            "Embed request sent to user with ID: $userId"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun sendChannelEmbed(channelId: String, embed: MessageEmbed): String {
        val channel = jda.getTextChannelById(channelId)
            ?: return "Channel with ID $channelId not found"
        return try {
            channel.sendMessageEmbeds(embed).submit().get()
            "Embed sent to channel with ID: $channelId"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}