package coroutines

import kotlinx.coroutines.*

fun main() {
    val scope = CoroutineScope(Job() + Dispatchers.Default)
    val job = scope.launch {
        try {
            delay(5000)
            println("finished")
        } catch (th: Throwable) {
            println(th.toString())
        }
    }
    runBlocking {
        delay(1000)
        job.cancel()
        job.join()
        delay(1000)
        println("isActive = ${scope.isActive}")
    }
}