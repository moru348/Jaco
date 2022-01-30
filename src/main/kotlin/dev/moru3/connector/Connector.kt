package dev.moru3.connector

import dev.moru3.Data
import dev.moru3.opcode.OpCode
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

open class Connector(val host: String, val path: String, private val autoReconnect: Boolean = false) {
    private val queues: Queue<String> = LinkedBlockingQueue()
    private val incomingListeners = mutableListOf<(String)->Unit>()
    val outGoing = object: OutGoing {
        override fun <T: Any> send(opCode: OpCode, message: T?) {
            sendMessage(Data.gson.toJson(OpCode.Base(opCode, message)))
        }
        override fun sendMessage(message: String) {
            queues.add(message.also { println("send: $it") })
        }
    }
    val inComing = object: InComing {
        override fun onReceive(func: (String) -> Unit) { incomingListeners.add(func) }
    }
    var isActive: Boolean = true
    protected val client = HttpClient(CIO) {
        install(WebSockets)
    }
    open fun onClose() {}
    init {
        thread { runBlocking {
            do {
                client.wss(urlString = "wss://${host}/${path}") {
                    while(true) {
                        if(!isActive) { break }
                        incoming.tryReceive().getOrNull()?.readBytes()?.also { incomingListeners.forEach { listener -> listener.invoke(String(it)) } }
                        try {
                            queues.poll()?.also { outgoing.send(Frame.Text(it)) }
                        } catch (e: CancellationException) {
                            onClose()
                            isActive = autoReconnect
                            break
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    this.close()
                }
            } while(autoReconnect)
        } }
    }

    interface OutGoing {
        fun <T: Any> send(opCode: OpCode, message: T?)

        fun sendMessage(message: String)
    }

    interface InComing {
        fun onReceive(func: (String)->Unit)
    }
}