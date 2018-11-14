import kotlinx.coroutines.*

class FileBuffer(bufferCapacity: Int = 25, private val sendCallback: suspend (String) -> Unit) {
    companion object {
        private const val TAG = "[FileBuffer]"
        private const val sendTimeout = 5000L
        private const val sendThreshold = 5
    }

    private val buffer = RingBuffer(capacity = bufferCapacity)
    private var trigger = CompletableDeferred<Boolean>()

    init {
        loop()
    }

    fun add(msg: String) {
        println("$TAG Received: \"$msg\"")
        synchronized(buffer) {
            buffer.add(msg)
            enoughToSend(buffer.size)
        }
    }

    private fun loop() = GlobalScope.launch {
        while (true) {
            if (buffer.size < sendThreshold) {
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
        println("$TAG Sending messages...")
        val messages = ArrayList<String>(sendThreshold)
        synchronized(buffer) {
            messages.addAll(buffer.removeMany(sendThreshold))
        }
        messages.forEach { sendCallback(it) }
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

    private fun enoughToSend(size: Int) {
        println("$TAG Buffer size=$size")
        if (trigger.isActive && size >= sendThreshold) {
            println("$TAG Firing trigger (enough to send)")
            trigger.complete(true)
        }
    }
}
