package xyz.yamida.services.moderation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ModerationServiceApplication

fun main(args: Array<String>) {
    runApplication<ModerationServiceApplication>(*args)
}