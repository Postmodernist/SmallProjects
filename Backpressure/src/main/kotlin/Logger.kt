import kotlinx.coroutines.*

class Logger(private val httpService: HttpService) {
    private val bufferCapacity = 25
    private val sendThreshold = 5
    private val sendTimeout = 5000L

    private val buffer0 = EvictingQueue<String>(bufferCapacity)
    private var trigger = CompletableDeferred<Boolean>()
    private lateinit var timerJob: Job

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
            if (buffer0.size < sendThreshold) {
                listen()
            }
            if (buffer0.size > 0) {
                send()
            }
        }
    }

    private suspend fun listen() = coroutineScope {
        println("[Logger] Waiting...")
        trigger = CompletableDeferred()
        timerJob = launch { runTimer() }
        trigger.await()
        if (timerJob.isActive) {
            timerJob.cancel()
        }
    }

    private suspend fun send() {
        println("[Logger] Sending messages...")
        val messages = ArrayList<String>(sendThreshold)
        synchronized(buffer0) {
            val n = if (buffer0.size < sendThreshold) buffer0.size else sendThreshold
            repeat(n) {
                messages.add(buffer0.remove())
            }
        }
        messages.forEach { httpService.send(it) }
    }

    private suspend fun runTimer() {
        println("[Logger] Starting new timer")
        delay(sendTimeout)
        if (trigger.isActive) {
            println("[Logger] Firing trigger (timer)")
            trigger.complete(true)
        }
        println("[Logger] Timer finished")
    }

    private fun enoughToSend(size: Int) {
        println("[Logger] Buffer size=$size")
        if (trigger.isActive && size >= sendThreshold) {
            println("[Logger] Firing trigger (enough to send)")
            trigger.complete(true)
        }
    }
}