package dev.moru3.data.presence

import com.google.gson.annotations.SerializedName
import java.util.*

class Activity(
    /**
     * アクティビティ名
     */
    val name: String,
    /**
     * アクティビティの種類
     */
    @Transient
    val activityType: ActivityType,
    /**
     * typeがSTREAMINGの場合に使用されます。
     */
    val url: String? = null,

    @SerializedName("created_at")
    val createdAt: Long? = null,
    /**
     * ゲームの開始/終了時のタイムスタンプ
     */
    @Transient
    val timestamp: Date? = null,

    /**
     * ゲームのアプリケーションID
     */
    @SerializedName("application_id")
    val applicationId: Int? = null,
    /**
     * プレイヤーが現在行っていること
     */
    val details: String? = null,
    /**
     * プレイヤーの現在のパーティーのステータス
     */
    val state: String? = null,

    /**
     * カスタムステータスの絵文字
     */
    val emoji: ActivityType.Emoji? = null,

    /**
     * プレイヤーが参加しているパーティーの情報
     */
    val party: ActivityType.Party? = null,

    /**
     * プレゼンス用の画像とそのホバーテキスト
     */
    val assets: ActivityType.Assets? = null,

    /**
     * プレゼンスの参加と観戦のトークン
     */
    val secrets: ActivityType.Secrets? = null,

    /*:
    アクティビティがインスタンス化されたゲームセッションであるかどうか
     */
    val instance: Boolean? = null,

    /**
     * プレゼンスに表示するカスタムボタン(最大２つ)
     */
    val buttons: List<ActivityButton>? = null
    ) {
    val type = activityType.id
    private val timestamps: String? = timestamp?.toString()
    enum class ActivityType(val id: Int) {
        GAME(0),
        STREAMING(1),
        LISTENING(2),
        WATCHING(3),
        CUSTOM(4),
        COMPETING(5);

        companion object {
            val mappings = mutableMapOf<Int, ActivityType>()
            init { values().forEach { mappings[it.id] = it } }
            operator fun get(value: Int?) = mappings[value]
        }
        class Emoji(val name: String, val id: Int? = null, val animated: Boolean? = null)
        class Party(val id: String? = null, val size: List<Int>? = null)
        class Assets(
            @SerializedName("large_image") val largeImage: String?,
            @SerializedName("large_text") val largeText: String?,
            @SerializedName("small_image") val smallImage: String?,
            @SerializedName("small_text") val smallText: String?,
        )
        class Secrets(val join: String?, val spectate: String?, val match: String?)
    }
    enum class ActivityFlags(val id: Int) {
        INSTANCE(1 shl 0),
        JOIN(1 shl 1),
        SPECTATE(1 shl 2),
        JOIN_REQUEST(1 shl 3),
        SYNC(1 shl 4),
        PLAY(1 shl 5)
    }
    class ActivityButton(val label: String, val url: String)
}