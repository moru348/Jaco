package dev.moru3.data.guild.channel.voice

class VoiceStateUpdate(val guild_id: String, val channel_id: String, val self_mute: Boolean = false, val self_deaf: Boolean = false)