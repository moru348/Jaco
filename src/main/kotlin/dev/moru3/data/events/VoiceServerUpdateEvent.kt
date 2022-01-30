package dev.moru3.data.events

import dev.moru3.event.Event

class VoiceServerUpdateEvent(val token: String, val guild_id: String, val endpoint: String): Event