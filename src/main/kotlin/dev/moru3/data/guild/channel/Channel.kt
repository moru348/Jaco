package dev.moru3.data.guild.channel

import dev.moru3.connector.GatewayConnector
import kotlinx.coroutines.runBlocking

data class Channel(val connector: GatewayConnector, val id: String) {
    fun sendMessage(msg: String) {
        runBlocking {
            connector.post("/channels/$id/messages", "{\"content\": \"${msg}\"}")
        }
    }
}