import kotlinx.coroutines.*

class RamBuffer(bufferCapacity: Int = 25, private val sendCallback: suspend (String) -> Unit) {
    companion object {
        private const val TAG = "[RamBuffer]"
        private const val sendTimeout = 5000L
    }

    private val buffer = EvictingQueue<String>(bufferCapacity)
    private var trigger = CompletableDeferred<Boolean>()

    init {
        loop()
    }

    fun add(msg: String) {
        println("$TAG Received: \"$msg\"")
        synchronized(buffer) {
            buffer.add(msg)
            enoughToSend()
        }
    }

    private fun loop() = GlobalScope.launch {
        while (true) {
            if (buffer.size == 0) {
                listen()
            }
            if (buffer.size > 0) {
                send()
            }
        }
    }

    private suspend fun listen() = coroutineScope {
        println("$TAG Waiting...")
        trigger = CompletableDeferred()
        val timerJob = launch { runTimer() }
        trigger.await()
        if (timerJob.isActive) {
            timerJob.cancel()
        }
    }

    private suspend fun send() {
        println("$TAG Sending message...")
        var msg: String?
        synchronized(buffer) {
            msg = buffer.remove()
        }
        msg?.let { sendCallback(it) }
    }


    private suspend fun runTimer() {
        println("$TAG Starting new timer")
        delay(sendTimeout)
        if (trigger.isActive) {
            println("$TAG Firing trigger (timer)")
            trigger.complete(true)
        }
        println("$TAG Timer finished")
    }

    private fun enoughToSend() {
        if (trigger.isActive) {
            println("$TAG Firing trigger (enough to send)")
            trigger.complete(true)
        }
    }
}
