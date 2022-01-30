package dev.moru3.connector

import dev.moru3.Data.gson
import dev.moru3.opcode.dispatch.DispatchStructure
import dev.moru3.opcode.gateway.GatewayOpCode
import dev.moru3.opcode.hello.HelloStructure
import dev.moru3.opcode.gateway.identify.invalid_session.InvalidSessionStructure
import dev.moru3.opcode.OpCodeStructure
import dev.moru3.opcode.gateway.identify.IdentifyStructure
import dev.moru3.opcode.gateway.identify.Intent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

abstract class GatewayConnector(val token: String, autoReconnect: Boolean = true): Connector("gateway.discord.gg", "/?v=9&encoding=json", autoReconnect) {
    private var heartbeatInterval: Long = -1
    var heartbeat: Long? = null
        private set
    private val heartbeatTimer = Timer()
    private val baseUrl = "https://discord.com/api/v9"

    suspend fun post(url: String, message: String) {
        client.post<String>(baseUrl + url) {
            headers {
                append("Authorization", "Bot $token")
                append("Content-Type", "application/json")
            }
            body = message
        }
    }

    protected open fun onReady(helloStructure: HelloStructure) {
        // schedule of sending heartbeat
        heartbeatInterval = (helloStructure.heartbeat_interval / 1.3).toLong()
        heartbeatTimer.scheduleAtFixedRate(heartbeatInterval, heartbeatInterval) { outGoing.send(GatewayOpCode.HEARTBEAT,heartbeat) }
    }

    protected open fun onHeartBeatAck() { /** There's nothing here :) **/ }

    protected open fun onInvalidSession(invalidSessionStructure: InvalidSessionStructure) { /** Can be overridden. **/ }

    protected open fun onDispatch(dispatchEvent: DispatchStructure) { /** Require override to create event. **/ }

    init {
        outGoing.send(GatewayOpCode.IDENTIFY , IdentifyStructure(token, arrayOf(Intent.GUILDS, Intent.GUILD_MESSAGES, Intent.GUILD_VOICE_STATES)))
        inComing.onReceive {
            println("receive: ${gson.toJson(it)}")
            when(GatewayOpCode[gson.fromJson(it, OpCodeStructure::class.java).op]) {
                GatewayOpCode.DISPATCH -> {
                    val dispatchEvent = gson.fromJson(it, DispatchStructure::class.java)
                    heartbeat = dispatchEvent.s.toLong()
                    onDispatch(dispatchEvent)
                }
                GatewayOpCode.HEARTBEAT -> {
                    /** Now, I'm googling this. Please wait for implementation :[] **/
                }
                GatewayOpCode.RECONNECT -> {
                    /** Now, I'm investigating this. Please wait for implementation :\ **/
                }
                GatewayOpCode.INVALID_SESSION -> {
                    onInvalidSession(gson.fromJson(it, InvalidSessionStructure::class.java))
                }
                GatewayOpCode.HELLO -> {
                    onReady(gson.fromJson(it, ReceiveHelloStructure::class.java).d)
                }
                GatewayOpCode.HEARTBEAT_ACK -> {
                    onHeartBeatAck()
                }
                else -> { /** There&s nothing here :9 **/ }
            }
        }
    }

    private class ReceiveHelloStructure(val d: HelloStructure)
}