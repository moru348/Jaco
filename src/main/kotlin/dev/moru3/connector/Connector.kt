package dev.moru3.connector

import com.google.gson.JsonObject
import dev.moru3.Data.gson
import dev.moru3.data.EventType
import dev.moru3.data.OpCode
import dev.moru3.identify.Identify
import dev.moru3.identify.Intent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.cio.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.logging.Logger
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.thread

abstract class Connector(val token: String, val autoReconnect: Boolean = true) {
    private var heartbeatInterval: Long = -1
    private var heartbeat: Long? = null
    private val heartbeatTimer = Timer()
    private val baseUrl = "https://discord.com/api/v6"

    protected val logger = Logger.getLogger("BotApplication")

    private val queues: Queue<String> = LinkedBlockingQueue()
    private val runnableList = mutableListOf<(DefaultClientWebSocketSession)->Unit>()
    private val incomingRunnableList = mutableMapOf<OpCode?, MutableList<Pair<Boolean, (DefaultClientWebSocketSession, JsonObject?)->Unit>>>()

    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    fun <T: Any> sendAsJson(opCode: OpCode, message: T) {
        sendRawMessage(gson.toJson(OpCode.Base(opCode, message)))
    }

    suspend fun post(url: String, message: String) {
        println("POST!")
        println(baseUrl + url)
        println(
            client.post<String>(baseUrl + url) {
                headers {
                    append("Authorization", "Bot $token")
                    append("Content-Type", "application/json")
                }
                body = message
            }
        )
    }

    fun sendRawMessage(message: String) { queues.add(message.also { println("send: $it") }) }

    protected fun webSocketProcess(runnable: DefaultClientWebSocketSession.()->Unit) { runnableList.add(runnable) }

    protected fun incomingProcess(opCode: OpCode?, bool: Boolean, runnable: DefaultClientWebSocketSession.(JsonObject?)->Unit) { incomingRunnableList[opCode] = (incomingRunnableList[opCode]?:mutableListOf()).also { it.add(Pair(bool, runnable)) } }

    init {
        thread { runBlocking {
            do {
                sendAsJson(OpCode.IDENTIFY ,Identify(token, arrayOf(Intent.GUILDS, Intent.GUILD_MESSAGES)))
                client.wss(host = "gateway.discord.gg", path = "/?v=9&encoding=json", method = HttpMethod.Get) {
                    while(true) {
                        try {
                            incoming.tryReceive().getOrNull()?.readBytes()?.also { frameBytes ->
                                val json = gson.fromJson(String(frameBytes), JsonObject::class.java)
                                json["s"].also { if(!it.isJsonNull) { heartbeat = json["s"].asLong } }
                                val opcode = OpCode[json["op"].let { if(!it.isJsonNull) it.asInt else return@also }]
                                incomingRunnableList[opcode]?.forEach {
                                    val d = json["d"];if(it.first) { if(d.isJsonNull) { it.second.invoke(this, null) } else { it.second.invoke(this, d.asJsonObject) } } else { it.second.invoke(this, json) }
                                }
                                incomingRunnableList[null]?.forEach {
                                    val d = json["d"];if(it.first) { if(d.isJsonNull) { it.second.invoke(this, null) } else { it.second.invoke(this, d.asJsonObject) } } else { it.second.invoke(this, json) }
                                }
                            }
                        } catch (e: Exception) { e.printStackTrace() }
                        try { queues.poll()?.also { outgoing.send(Frame.Text(it)) } } catch (e: CancellationException) { break } catch (e: Exception) { e.printStackTrace() }
                    }
                    this.close()
                }
            } while(autoReconnect)
        } }
        incomingProcess(OpCode.HELLO, true) { json ->
            heartbeatInterval = checkNotNull(json?.get("heartbeat_interval")?.asLong) { "There was an unexpected field in the received content of `HELLO`." } / 2
            heartbeatTimer.scheduleAtFixedRate(heartbeatInterval, heartbeatInterval) { sendRawMessage(gson.toJson(HeartBeat(heartbeat))) }
        }
    }

    class HeartBeat(val d: Long?) { val op = OpCode.HEARTBEAT.code }
}