package xyz.yamida.services.moderation.controller.mute

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/moderation")
class MuteController(private val muteService: MuteService) {

    @GetMapping("/is-muted")
    fun isUserMuted(@RequestParam gameName: String): Boolean {
        return muteService.isMuted(gameName)
    }

}