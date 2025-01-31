package xyz.yamida.service.discord.controller.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/discord/users")
class UserController(val userService: UserService) {

    @GetMapping("/by-game-nickname")
    fun getUserIdByGameNickname(@RequestParam nickname: String): String {
        return userService.getDiscordIdByNickname(nickname)
    }
}