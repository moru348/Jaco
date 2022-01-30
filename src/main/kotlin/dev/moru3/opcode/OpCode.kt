package dev.moru3.opcode

interface OpCode {
    val code: Int

    class Base<T>(@Transient val opcode: OpCode, @Transient val message: T) {
        val op = opcode.code
        val d: T = message
    }
}