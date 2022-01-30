package dev.moru3.connector

import com.google.gson.JsonObject
import dev.moru3.Data.gson
import dev.moru3.opcode.hello.HelloStructure
import dev.moru3.opcode.OpCodeStructure
import dev.moru3.opcode.voice.ReadyStructure
import dev.moru3.opcode.voice.SessionDescriptionStructure
import dev.moru3.opcode.voice.identify.VoiceOpCode
import dev.moru3.opcode.voice.identify.VoiceIdentifyStructure
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

abstract class VoiceConnector(val token: String, endpoint: String, private val gatewayConnector: GatewayConnector, val userId: String, val serverId: String, val sessionId: String): Connector(endpoint, "?v=4", false) {
    private var heartbeatInterval: Long = -1
    private val heartbeatTimer = Timer()

    private fun onHello(helloStructure: HelloStructure) {
        // schedule of sending heartbeat
        heartbeatInterval = (helloStructure.heartbeat_interval / 1.3).toLong()
        heartbeatTimer.scheduleAtFixedRate(heartbeatInterval, heartbeatInterval) {
            println("SENDING VOICE HEARTBEAT...")
            outGoing.send(VoiceOpCode.HEARTBEAT,gatewayConnector.heartbeat)
        }
    }

    open fun onReady(readyStructure: ReadyStructure) { }

    open fun onSessionDescription(sessionDescriptionStructure: SessionDescriptionStructure) { }

    private fun onHeartBeatAck() { /** There's nothing here :) **/ }


    init {
        outGoing.send(VoiceOpCode.IDENTIFY, VoiceIdentifyStructure(token,userId,serverId,sessionId))
        println("CONNECTING TO VOICE SERVER...")
        inComing.onReceive {
            println("voice_receive: ${gson.toJson(it)}")
            when(VoiceOpCode[gson.fromJson(it, OpCodeStructure::class.java).op]) {
                VoiceOpCode.IDENTIFY -> { }
                VoiceOpCode.SELECT_PROTOCOL -> { }
                VoiceOpCode.READY -> {
                    onReady(gson.fromJson(gson.fromJson(it, JsonObject::class.java)["d"].asJsonObject.toString(), ReadyStructure::class.java))
                }
                VoiceOpCode.HEARTBEAT -> { }
                VoiceOpCode.SESSION_DESCRIPTION -> {
                    onSessionDescription(gson.fromJson(gson.fromJson(it, JsonObject::class.java)["d"].asJsonObject.toString(), SessionDescriptionStructure::class.java))
                }
                VoiceOpCode.SPEAKING -> { }
                VoiceOpCode.HEARTBEAT_ACK -> {
                    onHeartBeatAck()
                }
                VoiceOpCode.RESUME -> { }
                VoiceOpCode.HELLO -> {
                    onHello(gson.fromJson(it, ReceiveHelloStructure::class.java).d)
                }
                VoiceOpCode.RESUMED -> { }
                VoiceOpCode.CLIENT_DISCONNECT -> { }
                null -> { }
            }
        }
    }

    private class ReceiveHelloStructure(val d: HelloStructure)
}