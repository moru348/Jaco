package dev.moru3

import dev.moru3.Data.gson
import dev.moru3.connector.GatewayConnector
import dev.moru3.data.*
import dev.moru3.data.guild.channel.Channel
import dev.moru3.data.events.EventType.*
import dev.moru3.opcode.dispatch.DispatchStructure
import dev.moru3.opcode.hello.HelloStructure
import dev.moru3.data.events.VoiceServerUpdateEvent
import dev.moru3.data.events.VoiceStateUpdateEvent
import dev.moru3.opcode.gateway.GatewayOpCode
import dev.moru3.data.presence.Presence
import dev.moru3.data.guild.channel.voice.VoiceStateUpdate
import dev.moru3.event.BotInitializeEvent
import dev.moru3.event.EventManager
import dev.moru3.voice.VoiceClient

class BotApplication(token: String, val debug: Boolean): GatewayConnector(token) {

    lateinit var user: User
        private set

    constructor(token: String): this(token, false)

    val eventManager = EventManager(this)

    private var voiceClient: VoiceClient? = null

    override fun onReady(helloStructure: HelloStructure) {
        eventManager.call(BotInitializeEvent(this@BotApplication))
        super.onReady(helloStructure)
    }

    var _tmp: String? = null

    override fun onDispatch(dispatchEvent: DispatchStructure) {
        println(dispatchEvent.t)
        when(dispatchEvent.t) {
            READY -> {
                user = gson.fromJson(dispatchEvent.d["user"].asJsonObject.toString(), User::class.java)
                _tmp = dispatchEvent.d["session_id"].asString
            }
            ERROR -> { }
            GUILD_STATUS -> { }
            GUILD_CREATE -> { }
            CHANNEL_CREATE -> { }
            VOICE_CHANNEL_SELECT -> { }
            VOICE_SERVER_UPDATE -> {
                val data = gson.fromJson(dispatchEvent.d, VoiceServerUpdateEvent::class.java)
                if(voiceClient?.token!=data.token) {
                    voiceClient?.isActive = false
                    voiceClient = VoiceClient(data.token, data.endpoint,this, user.id,data.guild_id,_tmp!!)
                }
            }
            VOICE_STATE_UPDATE -> {
                val data = gson.fromJson(dispatchEvent.d, VoiceStateUpdateEvent::class.java)
            }
            VOICE_SETTINGS_UPDATE -> { }
            VOICE_CONNECTION_STATUS -> { }
            SPEAKING_START -> { }
            SPEAKING_STOP -> { }
            MESSAGE_CREATE -> { }
            MESSAGE_UPDATE -> { }
            MESSAGE_DELETE -> { }
            NOTIFICATION_CREATE -> { }
            ACTIVITY_JOIN -> { }
            ACTIVITY_SPECTATE -> { }
            ACTIVITY_JOIN_REQUEST -> { }
        }
    }

    var presence: Presence = Presence(Presence.StatusType.ONLINE, listOf(), null, false)
        set(value) {
            this.outGoing.send(GatewayOpCode.PRESENCE_UPDATE, value)
            field = value
        }

    fun getChannel(id: String) = Channel(this, id)

    fun updateVoiceState(voiceStateUpdate: VoiceStateUpdate) {
        this.outGoing.send(GatewayOpCode.VOICE_STATE_UPDATE, voiceStateUpdate)
    }
}