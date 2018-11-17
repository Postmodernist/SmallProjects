import datastructures.EvictingQueue
import kotlinx.coroutines.*

class RamBuffer(bufferCapacity: Int = 25, private val sendCallback: suspend (String) -> Unit) {
    companion object {
        private const val TAG = "RamBuffer"
    }

    private val buffer = EvictingQueue<String>(bufferCapacity)
    private var trigger = CompletableDeferred<Boolean>()

    init {
        loop()
    }

    fun add(msg: String) {
        Log.i(TAG, "Received: '$msg'")
        synchronized<Unit>(buffer) {
            buffer.add(msg)
        }
        trigger.complete(true)
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

    private suspend fun listen() = coroutineScope<Unit> {
        Log.i(TAG, "Waiting...")
        trigger = CompletableDeferred()
        trigger.await()
    }

    private suspend fun send() {
        val msg: String
        synchronized(buffer) {
            msg = buffer.remove()
        }
        Log.i(TAG, "Sending '$msg'")
        sendCallback(msg)
    }
}
