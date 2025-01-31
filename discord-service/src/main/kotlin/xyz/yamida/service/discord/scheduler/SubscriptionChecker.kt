package xyz.yamida.service.discord.scheduler

import net.dv8tion.jda.api.JDA
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import xyz.yamida.service.discord.repository.UserRepository

@Component
class SubscriptionChecker(
    val userRepository: UserRepository,
    val jda: JDA
) {
    val notificationDaysBeforeEnd = 4

    @Scheduled(cron = "0 0 0 * * ?")
    fun checkSubscriptions() {
        println("Запуск проверки подписок...")

        val users = userRepository.findAll()

        users.forEach { user ->
            val subscribeDays = user.subscribeDays

            if (subscribeDays > 0) {
                when (subscribeDays) {
                    notificationDaysBeforeEnd -> {
                        notifyUser(user.discordId, subscribeDays)
                    }
                    0 -> {
                        notifyUser(user.discordId, 0)
                    }
                }

                user.subscribeDays = (subscribeDays - 1).coerceAtLeast(0)
                userRepository.save(user)
            }
        }
    }

    fun notifyUser(discordId: String, daysLeft: Int) {
        val user = jda.getUserById(discordId)
        if (user != null) {
            val message = when {
                daysLeft > 0 -> "🔔 Привет! Ваша подписка заканчивается через $daysLeft дня(-ей). Не забудьте продлить её!"
                daysLeft == 0 -> "⚠️ Привет! Ваша подписка закончилась. Продлите её, чтобы продолжить пользоваться услугами!"
                else -> "❌ Произошла ошибка при расчёте дней до окончания подписки."
            }
            user.openPrivateChannel()
                .flatMap { it.sendMessage(message) }
                .queue(
                    { println("Уведомление успешно отправлено пользователю с ID: $discordId") },
                    { println("Не удалось отправить уведомление пользователю с ID: $discordId") }
                )
        } else {
            println("Пользователь с ID $discordId не найден в Discord. Возможно, он вышел с сервера.")
        }
    }
}
