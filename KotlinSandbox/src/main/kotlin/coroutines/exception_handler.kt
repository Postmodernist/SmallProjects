package coroutines

import kotlinx.coroutines.*

fun main() = runBlocking {
    val handler = CoroutineExceptionHandler { _, throwable ->
        println("[handler] $throwable")
    }

    val job = GlobalScope.launch(handler) {
        repeat(3) {
            println("Crashing in $it")
            delay(1000)
        }
        error("Bang!")
    }

    job.join()
}
