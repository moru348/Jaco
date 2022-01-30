package dev.moru3.opcode.voice.identify

import dev.moru3.opcode.OpCode

enum class VoiceOpCode(override val code: Int): OpCode {
    IDENTIFY(0),
    SELECT_PROTOCOL(1),
    READY(2),
    HEARTBEAT(3),
    SESSION_DESCRIPTION(4),
    SPEAKING(5),
    HEARTBEAT_ACK(6),
    RESUME(7),
    HELLO(8),
    RESUMED(9),
    CLIENT_DISCONNECT(13);

    companion object {
        private val mapping = mutableMapOf<Int, VoiceOpCode>()
        init {
            values().forEach { mapping[it.code] = it }
        }
        operator fun get(code: Int?): VoiceOpCode? = mapping[code]
    }
}