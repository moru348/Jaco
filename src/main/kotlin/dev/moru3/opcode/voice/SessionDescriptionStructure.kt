package dev.moru3.opcode.voice

class SessionDescriptionStructure(val vodeo_codec: String, val secret_key: ByteArray, val mode: String, val media_session_id: String, val audio_code: String)