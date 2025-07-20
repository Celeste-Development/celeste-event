package celestedevelopment.event

interface IListener : Comparable<Listener>
{
    fun <T : Any> invoke(event: T)
}