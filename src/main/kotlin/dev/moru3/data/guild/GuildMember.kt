package dev.moru3.data.guild

import dev.moru3.data.User
import java.util.*

class GuildMember(
    val user: User,
    val nick: String?,
    val avatar: String?,
    val roles: List<String>,
    val joined_at: Date,
    val premium_since: Date?,
    val deaf: Boolean,
    val mute: Boolean,
    val pending: Boolean?,
    val permissions: String,
    val communication_disabled_until: Date?
    )