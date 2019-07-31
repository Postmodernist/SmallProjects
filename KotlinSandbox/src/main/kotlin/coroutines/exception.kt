package coroutines

import kotlinx.coroutines.*

class ExceptionInCoroutine {

    private val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Catch $throwable")
    }

    private val scope = CoroutineScope(Job() + Dispatchers.IO + handler)

    fun run() {
        scope.launch {
            delay(1000)
            throw IllegalStateException("Boom!")
        }
    }

}

fun main() {
    ExceptionInCoroutine().run()
    runBlocking { delay(3000) }
    println("Finished")
}