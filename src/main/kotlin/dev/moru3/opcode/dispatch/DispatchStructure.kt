package dev.moru3.opcode.dispatch

import com.google.gson.JsonObject
import dev.moru3.data.events.EventType

class DispatchStructure(val t: EventType?, val s: Int, val d: JsonObject)