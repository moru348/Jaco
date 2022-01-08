package dev.moru3.data

import dev.moru3.connector.Connector
import kotlinx.coroutines.runBlocking

data class Channel(val connector: Connector ,val id: String) {
    fun sendMessage(msg: String) {
        runBlocking {
            connector.post("/channels/$id/messages", "{\"content\": \"${msg}\"}")
        }
    }
}