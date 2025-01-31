package xyz.yamida.service.discord.controller.punishments

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/discord/punishment")
class PunishmentController(
    val punishmentService: PunishmentService
) {

    @PostMapping("/add-warn")
    fun addWarn(
        @RequestParam userId: String,
        @RequestParam reason: String,
        @RequestParam count: Int = 1
    ): String {
        if (count < 0) {
            return "Invalid count of warnings: $count"
        }

        return punishmentService.addWarnAndNotify(userId, reason, count)
    }

    @PostMapping("/remove-warn")
    fun removeWarn(@RequestParam userId: String): String {
        return "Not working yet"
    }
}
