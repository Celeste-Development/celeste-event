package com.celeste.event

import kotlin.reflect.KClass

class Listener(
    val listener : (Any) -> Unit,
    val type : KClass<*>,
    val priority : Int,
    val parallel : Boolean,
    val receiveCancelled : Boolean
) : IListener
{
    override fun compareTo(other: Listener) : Int
    {
        return other.priority.compareTo(this.priority)
    }

    override fun <T : Any> invoke(event: T)
    {
        (listener as (T) -> Unit)(event)
    }
}

inline fun <reified T : Any> listener(
    priority : Int = EventPriority.NORMAL,
    parallel : Boolean = false,
    receiveCancelled : Boolean = false,
    noinline listener : (T) -> Unit
)
: Listener = Listener(
    listener as (Any) -> Unit,
    T::class,
    priority,
    parallel,
    receiveCancelled
)