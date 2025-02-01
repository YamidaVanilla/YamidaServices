package xyz.yamida.services.moderation.controller.ban

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/moderation")
class BanController(
    val banService: BanService
) {
    @GetMapping("/is-baned")
    fun isUserBaned(@RequestParam gameName: String): String?{
        return banService.isBanned(gameName)
    }
}