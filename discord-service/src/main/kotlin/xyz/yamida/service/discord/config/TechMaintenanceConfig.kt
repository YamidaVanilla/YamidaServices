package xyz.yamida.service.discord.config

import org.springframework.context.annotation.Configuration
import java.util.concurrent.atomic.AtomicBoolean

@Configuration
class TechMaintenanceConfig {
    private val mode = AtomicBoolean(false)

    fun enable() {
        mode.set(true)
    }

    fun disable() {
        mode.set(false)
    }

    fun isEnabled(): Boolean {
        return mode.get()
    }
}
