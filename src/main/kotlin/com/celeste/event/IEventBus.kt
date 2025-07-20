package com.celeste.event

interface IEventBus
{
    fun <T : Any> post(event : T): Boolean

    fun register(listener : Listener)

    fun unregister(listener : Listener)

    fun subscribe(obj : Any)

    fun unsubscribe(obj : Any)

    fun clear()

    fun shutdown()
}
