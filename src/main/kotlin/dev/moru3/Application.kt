package dev.moru3

import dev.moru3.data.Activity
import dev.moru3.data.Channel
import dev.moru3.data.Presence
import dev.moru3.event.BotInitializeEvent
import dev.moru3.event.EventListener
import dev.moru3.event.Listener


fun main() {
    Test()
}


class Test: Listener {

    @EventListener
    fun onInit(event: BotInitializeEvent) {
        Thread.sleep(1000)
        Channel(event.botApplication, "751773947238023309").sendMessage("うんち")
    }

    init {
        val user = BotApplication(System.getenv("DISCORD_TOKEN"), true)
        user.eventManager.registerEvents(this)
    }
}
