package dev.moru3.data

import dev.moru3.SerializeNull

class User(
    val id: String,
    val username: String,
    val discriminator: String,
    @SerializeNull val avatar: String?,
    val bot: Boolean?,
    val system: Boolean?,
    val mfa_enabled: Boolean?,
    @SerializeNull val banner: String?,
    @SerializeNull val accent_color: Int?,
    val locale: String?,
    val verified: Boolean?,
    val email: String?,
    val flags: Int?,
    val premium_type: Int?,
    val public_flag: Int?
    )