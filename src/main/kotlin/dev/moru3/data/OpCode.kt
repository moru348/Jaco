package dev.moru3.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import dev.moru3.Data.gson

enum class OpCode(val code: Int) {
    DISPATCH(0),
    HEARTBEAT(1),
    IDENTIFY(2),
    PRESENCE_UPDATE(3),
    VOICE_STATE_UPDATE(4),
    RESUME(6),
    RECONNECT(7),
    REQUEST_GUILD_MEMBERS(8),
    INVALID_SESSION(9),
    HELLO(10),
    HEARTBEAT_ACK(11);

    companion object {
        private val mapping = mutableMapOf<Int, OpCode>()
        init {
            values().forEach { mapping[it.code] = it }
        }
        operator fun get(code: Int?): OpCode? = mapping[code]
    }

    class Base<T>(@Transient val opcode: OpCode, @Transient val message: T) {
        val op = opcode.code
        val d: T = message
    }

}