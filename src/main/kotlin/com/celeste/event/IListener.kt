package com.celeste.event

interface IListener : Comparable<Listener>
{
    fun <T : Any> invoke(event: T)
}