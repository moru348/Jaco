package dev.moru3

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter


object Data {
    val gson = GsonBuilder().registerTypeAdapterFactory(object: TypeAdapterFactory {
        val typeAdapterFactory = this
        override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
            val fields = type.rawType.declaredFields
            val nullableFieldNames = fields
                .filter { it.annotations.filterIsInstance(SerializeNull::class.java).isNotEmpty() }
                .map { it.annotations.filterIsInstance(SerializedName::class.java).firstOrNull()?.value?:it.name }
            val nonNullableFieldNames = fields.map { it.annotations.filterIsInstance(SerializedName::class.java).firstOrNull()?.value?:it.name }.minus(nullableFieldNames.toSet())
            // if(nullableFieldNames.isEmpty()) { return null }
            // if(type.rawType.name.startsWith("java.lang")) { return null }
            return object: TypeAdapter<T>() {
                private val delegateAdapter = gson.getDelegateAdapter(typeAdapterFactory, type)
                private val elementAdapter = gson.getAdapter(JsonElement::class.java)

                override fun read(p0: JsonReader?): T = delegateAdapter.read(p0)

                override fun write(p0: JsonWriter, p1: T?) {
                    try {
                        val jsonObject = delegateAdapter.toJsonTree(p1).asJsonObject
                        nonNullableFieldNames.filter { jsonObject[it] is JsonNull }
                            .forEach { jsonObject.remove(it) }
                        val originalSerializeNulls = p0.serializeNulls
                        p0.serializeNulls = true
                        elementAdapter.write(p0, jsonObject)
                        p0.serializeNulls = originalSerializeNulls
                    } catch (e: Exception) {
                        elementAdapter.write(p0, delegateAdapter.toJsonTree(p1))
                    }
                }
            }
        }
    }).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create()
}