package celestedevelopment.event

interface ICancellable
{
    var cancelled: Boolean

    fun cancel()
    {
        cancelled = true
    }

    fun isCancelled(): Boolean
}