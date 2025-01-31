package xyz.yamida.services.profile.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.yamida.services.profile.service.UserService

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/authorized")
    fun isAuthorized(
        @RequestParam(required = false) discordId: String?,
        @RequestParam(required = false) nickname: String?
    ): ResponseEntity<Boolean> {
        val isAuthorized = when {
            discordId != null -> userService.isAuthorizedByDiscordId(discordId)
            nickname != null -> userService.isAuthorizedByNickname(nickname)
            else -> return ResponseEntity.badRequest().body(false)
        }
        return ResponseEntity.ok(isAuthorized)
    }

    @GetMapping("/subscription/days-left")
    fun getSubscriptionDaysLeft(
        @RequestParam(required = false) discordId: String?,
        @RequestParam(required = false) nickname: String?
    ): ResponseEntity<Map<String, Any>> {
        val daysLeft = when {
            discordId != null -> userService.getSubscriptionDaysLeftByDiscordId(discordId)
            nickname != null -> userService.getSubscriptionDaysLeftByNickname(nickname)
            else -> return ResponseEntity.badRequest().body(mapOf("error" to "Необходимо указать discordId или nickname"))
        }

        return if (daysLeft != null) {
            ResponseEntity.ok(mapOf("daysLeft" to daysLeft))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}