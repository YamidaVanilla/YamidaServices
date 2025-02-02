package xyz.yamida.service.discord.services.punishments

import net.dv8tion.jda.api.EmbedBuilder
import org.springframework.stereotype.Service
import xyz.yamida.service.discord.services.messaging.MessagingService
import xyz.yamida.service.discord.repository.UserRepository

@Service
class PunishmentService(
    val userRepository: UserRepository,
    val messagingService: MessagingService
) {
    fun addWarnAndNotify(userId: String, reason: String, count: Int): String {
        val user = userRepository.findByDiscordId(userId)
            ?: return "User with id $userId not found"


        user.warnCount += count
        userRepository.save(user)

        val embed = EmbedBuilder()
            .setTitle("Система модерации")
            .setDescription(
                """
                **Вы получили** `$count` **предупреждений.**
                **Причина:** `$reason`
                **Текущее количество предупреждений:** `${user.warnCount}`
                """.trimIndent()
            )
            .setColor(0xF695CB)
            .build()

        return try {
            messagingService.sendDirectEmbed(userId, embed)
            "$count warning${if (count > 1) "s were" else " was"} added for user $userId. Notification sent."
        } catch (e: Exception) {
            "$count warning${if (count > 1) "s were" else " was"} added for user $userId. **Notification failed:** ${e.message}"
        }
    }
}