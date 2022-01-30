package dev.moru3.opcode.gateway

import dev.moru3.opcode.OpCode

enum class GatewayOpCode(override val code: Int): OpCode {
    DISPATCH(0), //DispatchStructure
    HEARTBEAT(1), //Long
    IDENTIFY(2), //IdentifyStructure
    PRESENCE_UPDATE(3),
    VOICE_STATE_UPDATE(4),
    RESUME(6),
    RECONNECT(7),
    REQUEST_GUILD_MEMBERS(8),
    INVALID_SESSION(9),//InvalidSessionStructure
    HELLO(10), //HelloStructure
    HEARTBEAT_ACK(11); //ignore

    companion object {
        private val mapping = mutableMapOf<Int, GatewayOpCode>()
        init {
            values().forEach { mapping[it.code] = it }
        }
        operator fun get(code: Int?): GatewayOpCode? = mapping[code]
    }
}