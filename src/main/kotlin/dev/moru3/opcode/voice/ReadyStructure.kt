package dev.moru3.opcode.voice

class ReadyStructure(val streams: List<StreamStructure>, val ssrc: Int, val port: Int, val modes: List<String>, val ip: String, val experiments: List<String>)

class StreamStructure(val type: String, val ssrc: Int, val rtx_ssrc: Int, val rid: String, val quality: Int, val active: Boolean)