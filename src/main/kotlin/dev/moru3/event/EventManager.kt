package dev.moru3.event

import dev.moru3.connector.GatewayConnector
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.Executors

open class EventManager(val connector: GatewayConnector) {

    val listeners = mutableMapOf<Class<*>, TreeMap<Int, MutableList<Pair<Any, Method>>>>()

    val executor = Executors.newSingleThreadExecutor()

    fun registerEvents(listener: Listener) {
        listener::class.java.declaredMethods.forEach { method ->
            val param = (method.parameters.takeIf { it.size==1 }?.let { arrayOfParameters -> arrayOfParameters.getOrNull(0)?.takeIf { Event::class.java.isAssignableFrom(it.type) }?:return@forEach }?:return@forEach).type
            val annotation = (method.annotations.filter { it is EventListener }.firstOrNull()?:return@forEach) as EventListener
            listeners[param] = (listeners[param]?:TreeMap()).also { map -> map[annotation.priority] = (map[annotation.priority]?:mutableListOf()).also { it.add(Pair(listener, method)) } }
        }
    }

    fun call(event: Event) {
        val listeners = listeners[event::class.java]?:return
        (listeners).forEach { entry -> entry.value.forEach { executor.submit { it.second.invoke(it.first, event) } } }
    }
}