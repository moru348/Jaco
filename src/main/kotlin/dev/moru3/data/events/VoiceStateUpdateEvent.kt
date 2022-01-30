package dev.moru3.data.events

import dev.moru3.SerializeNull
import dev.moru3.data.guild.GuildMember
import dev.moru3.event.Event
import java.util.*

class VoiceStateUpdateEvent(
    val guild_id: String?,
    @SerializeNull val channel_id: String?,
    val user_id: String,
    val member: GuildMember,
    val session_id: String,
    val deaf: Boolean,
    val mute: Boolean,
    val self_deaf: Boolean,
    val self_mute: Boolean,
    val self_stream: Boolean?,
    val self_video: Boolean,
    val suppress: Boolean,
    @SerializeNull val request_to_speak_timestamp: Date): Event