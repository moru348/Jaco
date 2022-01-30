package dev.moru3

import dev.moru3.Data.gson
import dev.moru3.data.presence.Presence
import dev.moru3.data.guild.channel.voice.VoiceStateUpdate
import dev.moru3.event.BotInitializeEvent
import dev.moru3.event.EventListener
import dev.moru3.event.Listener


fun main(args: Array<String>) {
    Test(args[0])
}


class Test(token: String): Listener {

    @EventListener
    fun onInit(event: BotInitializeEvent) {
        Thread.sleep(1000)
        println(gson.toJson(event.botApplication.user))
        event.botApplication.updateVoiceState(VoiceStateUpdate("751013010944032809", "904026748457480233"))
    }

    init {
        val user = BotApplication(token, true)
        user.eventManager.registerEvents(this)
    }
}
