package dev.moru3.opcode.voice

class SelectProtocolStructure(val protocol: String, address: String, port: Int, mode: String) {
    val data = Data(address, port, mode)
    class Data(val address: String, val port: Int, val mode: String)
}