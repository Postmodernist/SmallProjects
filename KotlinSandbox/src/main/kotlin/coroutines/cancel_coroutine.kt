package coroutines

import kotlinx.coroutines.*

fun main() {
    val scope = CoroutineScope(Job() + Dispatchers.IO)
    val job = scope.launch {
        while (isActive) {
            print(". ")
            @Suppress("BlockingMethodInNonBlockingContext")
            Thread.sleep(500)
        }
    }

    runBlocking {
        delay(2000)
        scope.cancel()
        job.join()
    }

    println("finished")
}