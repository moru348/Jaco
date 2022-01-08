package dev.moru3.identify

import com.google.gson.annotations.SerializedName
import dev.moru3.data.OpCode

class Identify(
    val token: String,
    @Transient
    val intentList: Array<out Intent>
) {
    var intents: Int = 0
        private set

    val properties = Properties()

    init { intentList.map(Intent::integer).forEach { intents += it } }
    class Properties {
        @SerializedName("\$os")
        val os: String = System.getProperty("os.name")?:"None"
        @SerializedName("\$browser")
        val browser = "moru3_original_library"
        @SerializedName("\$device")
        val device = browser
    }
}