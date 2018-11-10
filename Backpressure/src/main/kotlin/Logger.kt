import kotlinx.coroutines.*

class Logger(private val httpService: HttpService) {
    private val sendTimeout = 5000L  // milliseconds
    private val sendSize = 5

    private val buffer0 = EvictingQueue<String>(25)
    private var trigger: CompletableDeferred<Boolean>? = null
    private var timerJob: Job? = null

    init {
        loop()
    }

    fun log(msg: String) {
        println("[Logger] Received: \"$msg\"")
        synchronized(buffer0) {
            buffer0.add(msg)
            enoughToSend(buffer0.size)
        }
    }

    private fun loop() = GlobalScope.launch {
        while (true) {
            if (buffer0.size < sendSize) {
                listen()
            }
            if (buffer0.size > 0) {
                send()
            }
        }
    }

    private suspend fun listen() {
        println("[Logger] Waiting...")
        timerJob = coroutineScope { launch { runTimer() } }
        trigger?.await()
        timerJob?.cancel()
        trigger = CompletableDeferred()
    }

    private suspend fun send() {
        println("[Logger] Sending messages...")
        val size = if (buffer0.size < sendSize) buffer0.size else sendSize
        val messages = ArrayList<String>(size)
        synchronized(size) {
            repeat (size) {
                messages.add(buffer0.remove())
            }
        }
        messages.forEach { httpService.send(it) }
    }

    private suspend fun runTimer() {
        println("[Logger] Starting new timer")
        delay(sendTimeout)
        println("[Logger] Timer finished")
        trigger?.complete(true)
    }

    private fun enoughToSend(size: Int) {
        println("[Logger] Buffer size=$size")
        if (size >= sendSize) {
            trigger?.complete(true)
        }
    }
}