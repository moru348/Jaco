package dev.moru3.opcode.gateway.identify


enum class Intent(b: Int) {
    /**
     * ギルド: 作成,更新,削除
     * ギルドのロール: 作成,更新,削除
     * チャンネル: 作成,更新,削除
     * スレッド: 作成,更新,削除,リストの同期,メンバーの更新,メンバーの更新*
     */
    GUILDS( 0),

    /**
     * メンバー: 追加,更新,退出
     * スレッド: メンバーの更新*
     */
    GUILD_MEMBERS(1),

    /**
     * BAN: 追加,削除
     */
    GUILD_BANS(2),

    /**
     * 絵文字の更新
     * ステッカーの更新
     */
    GUILD_EMOJIS_AND_STICKERS(3),

    /**
     * ギルドのインテグレーション: アップデート
     * インテグレーション: 作成,更新,削除
     */
    GUILD_INTEGRATIONS(4),

    /**
     * webhookのアップデート
     */
    GUILD_WEBHOOKS(5),

    /**
     * 招待: 作成,削除
     */
    GUILD_INVITES(6),

    /**
     * ボイスちゃんねるのステータス: アップデート
     */
    GUILD_VOICE_STATES(7),

    /**
     * ギルドのプリセンス: アップデート
     */
    GUILD_PRESENCES(8),

    /**
     * メッセージ: 作成,更新,削除,削除(BULK)
     */
    GUILD_MESSAGES(9),

    /**
     * リアクション: 追加,削除,すべて削除,絵文字の削除(?)
     */
    GUILD_MESSAGE_REACTIONS(10),

    /**
     * Typing start
     */
    GUILD_MESSAGE_TYPING(11),

    /**
     * DM: 作成,更新,削除,PING
     */
    DIRECT_MESSAGES(12),

    /**
     * DMのリアクション: 追加,削除,すべて削除,絵文字の削除(?)
     */
    DIRECT_MESSAGE_REACTIONS(13),

    /**
     * DM Typing start
     */
    DIRECT_MESSAGE_TYPING(14);

    val integer = 1 shl b
}