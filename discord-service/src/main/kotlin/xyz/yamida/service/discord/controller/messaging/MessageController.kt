package xyz.yamida.service.discord.controller.messaging

import net.dv8tion.jda.api.EmbedBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/discord/send")
class MessageController(val messagingService: MessagingService) {

    @PostMapping("/dm")
    fun sendDirectMessage(@RequestParam userId: String, @RequestParam message: String): String {
        return messagingService.sendDirectMessage(userId, message)
    }

    @PostMapping("/channel")
    fun sendChannelMessage(@RequestParam channelId: String, @RequestParam message: String): String {
        return messagingService.sendChannelMessage(channelId, message)
    }

    @PostMapping("/dm/embed")
    fun sendDirectEmbed(
        @RequestParam userId: String,
        @RequestParam title: String,
        @RequestParam description: String,
        @RequestParam(required = false) color: String?
    ): String {
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .apply {
                if (color != null) setColor(java.awt.Color.decode(color))
            }
            .build()
        return messagingService.sendDirectEmbed(userId, embed)
    }

    @PostMapping("/channel/embed")
    fun sendChannelEmbed(
        @RequestParam channelId: String,
        @RequestParam title: String,
        @RequestParam description: String,
        @RequestParam(required = false) color: String?
    ): String {
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .apply {
                if (color != null) setColor(java.awt.Color.decode(color))
            }
            .build()
        return messagingService.sendChannelEmbed(channelId, embed)
    }
}
