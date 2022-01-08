package dev.moru3.event

@Target(AnnotationTarget.FUNCTION)
annotation class EventListener(val priority: Int = Int.MAX_VALUE/2)