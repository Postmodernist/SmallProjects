import datastructures.RingBuffer
import kotlinx.coroutines.*
import java.io.File

class FileBuffer(bufferCapacity: Int = 25, private val sendCallback: suspend (String) -> Unit) {
    companion object {
        private const val TAG = "FileBuffer"
        private const val sendTimeout = 5000L
        private const val sendThreshold = 5
    }

    private val buffer = RingBuffer(File("ringbuffer"), bufferCapacity)
    private var trigger = CompletableDeferred<Boolean>()

    init {
        loop()
    }

    fun add(msg: String) {
        Log.i(TAG, "Received: '$msg'")
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
        Log.i(TAG, "Waiting...")
        trigger = CompletableDeferred()
        val timerJob = launch { runTimer() }
        trigger.await()
        if (timerJob.isActive) {
            timerJob.cancel()
        }
    }

    private suspend fun send() {
        Log.i(TAG, "Sending messages...")
        val messages = ArrayList<String>(sendThreshold)
        synchronized(buffer) {
            messages.addAll(buffer.removeMany(sendThreshold))
        }
        messages.forEach { sendCallback(it) }
    }

    private suspend fun runTimer() {
        Log.i(TAG, "Starting new timer")
        delay(sendTimeout)
        if (trigger.isActive) {
            Log.i(TAG, "Firing trigger (timer)")
            trigger.complete(true)
        }
        Log.i(TAG, "Timer finished")
    }

    private fun enoughToSend(size: Int) {
        Log.i(TAG, "Buffer size=$size")
        if (trigger.isActive && size >= sendThreshold) {
            Log.i(TAG, "Firing trigger (enough to send)")
            trigger.complete(true)
        }
    }
}
