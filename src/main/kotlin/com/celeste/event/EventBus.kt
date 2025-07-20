package com.celeste.event

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

class EventBus() : IEventBus
{
    private val listeners = ConcurrentHashMap<KClass<*>, CopyOnWriteArrayList<Listener>>()
    private val subscribers = ConcurrentHashMap<Any, List<Listener>>()
    private val parallelScope : CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun <T : Any> post(event : T): Boolean
    {
        val sorted = (listeners[event::class] ?: return false).sorted()
        sorted.filter { !it.parallel }.forEach {
            if (event !is ICancellable || !event.isCancelled() || it.receiveCancelled)
            {
                try { it.invoke(event) }
                catch (e : Exception) { e.printStackTrace() }
            }
        }

        val parallel = sorted.filter { it.parallel }
        if (parallel.isNotEmpty())
        {
            parallel.forEach {
                if (event !is ICancellable || !event.isCancelled() || it.receiveCancelled)
                {
                    parallelScope.launch {
                        try { it.invoke(event) }
                        catch (e : Exception) { e.printStackTrace() }
                    }
                }
            }
        }

        return if (event is ICancellable) event.isCancelled() else false
    }

    override fun register(listener : Listener)
    {
        listeners.computeIfAbsent(listener.type) { CopyOnWriteArrayList() }.add(listener)
    }

    override fun unregister(listener : Listener)
    {
        listeners[listener.type]?.remove(listener)
    }

    override fun subscribe(obj : Any)
    {
        if (subscribers.containsKey(obj)) return

        val found = mutableListOf<Listener>()
        obj::class.memberProperties.forEach {
            try
            {
                val value = it.getter.call(obj)
                if (value is Listener)
                {
                    found.add(value)
                    register(value)
                }
            }
            catch (e: Exception) { e.printStackTrace() }
        }

        obj::class.memberFunctions.forEach {

            if (it.returnType.classifier == Listener::class && it.parameters.size == 1 && it.parameters[0].kind == KParameter.Kind.INSTANCE) {
                try
                {
                    val value = it.call(obj)
                    if (value is Listener)
                    {
                        found.add(value)
                        register(value)
                    }
                }
                catch (e : Exception) { e.printStackTrace() }
            }
        }

        subscribers[obj] = found
    }

    override fun unsubscribe(obj : Any)
    {
        (subscribers.remove(obj) ?: return).forEach {
            unregister(it)
        }
    }

    override fun clear()
    {
        listeners.clear()
        subscribers.clear()
    }

    override fun shutdown()
    {
        parallelScope.cancel()
    }
}