package dev.moru3.opcode.gateway.identify.invalid_session

import com.google.gson.annotations.SerializedName

class InvalidSessionStructure(@SerializedName("d") val resumable: Boolean)