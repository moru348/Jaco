package dev.moru3.data

import dev.moru3.SerializeNull

data class Presence(@Transient val statusType: StatusType, val activities: List<Activity> = listOf(), @SerializeNull var since: Int? = null, var afk: Boolean = false) {

    val status = statusType.toString()

    enum class StatusType {
        ONLINE, // 緑色のマーク。通常時。
        DND, // 赤色のマーク。取り込み中。
        IDLE, // 黄色のマーク。放置中。
        INVISIBLE, // 灰色のマーク。オンライン状態を隠している。
        OFFLINE // 灰色のマーク。オフライン。
        ;

        override fun toString(): String = super.toString().lowercase()
    }
}