package dev.moru3

import dev.moru3.Data.gson
import dev.moru3.connector.Connector
import dev.moru3.data.EventType.*
import dev.moru3.data.OpCode
import dev.moru3.data.Presence
import dev.moru3.event.BotInitializeEvent
import dev.moru3.event.EventManager
import java.util.logging.Level

class BotApplication(token: String, val debug: Boolean): Connector(token) {

    constructor(token: String): this(token, false)

    val eventManager = EventManager(this)

    init {
        if(debug) { logger.level = Level.ALL }
        incomingProcess(null, false) {
            println("receive: ${gson.toJson(it)}")
        }
        incomingProcess(null, false) {
            if(it==null) { return@incomingProcess }
            val type = try { valueOf(it["t"].let { if(it.isJsonNull) return@incomingProcess else it.asString }) } catch(e: Exception) { return@incomingProcess }
            when(type) {
                READY -> { eventManager.call(BotInitializeEvent(this@BotApplication)) }
                ERROR -> {  }
                GUILD_STATUS -> {  }
                GUILD_CREATE -> {  }
                CHANNEL_CREATE -> {  }
                VOICE_CHANNEL_SELECT -> {  }
                VOICE_STATE_CREATE -> {  }
                VOICE_STATE_UPDATE -> {  }
                VOICE_STATE_DELETE -> {  }
                VOICE_SETTINGS_UPDATE -> {  }
                VOICE_CONNECTION_STATUS -> {  }
                SPEAKING_START -> {  }
                SPEAKING_STOP -> {  }
                MESSAGE_CREATE -> {  }
                MESSAGE_UPDATE -> {  }
                MESSAGE_DELETE -> {  }
                NOTIFICATION_CREATE -> {  }
                ACTIVITY_JOIN -> {  }
                ACTIVITY_SPECTATE -> {  }
                ACTIVITY_JOIN_REQUEST -> {  }
            }
        }
    }

    var presence: Presence = Presence(Presence.StatusType.ONLINE, listOf(), null, false)
        set(value) {
            this.sendAsJson(OpCode.PRESENCE_UPDATE, value)
            field = value
        }
}