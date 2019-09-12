package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.Executors

private suspend fun fooAsync(): Deferred<String> {
    val scope = CoroutineScope(Job() + Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    return scope.async {
        delay(1000)
        error("Failure")
    }
}

fun main() = runBlocking {
    val result = fooAsync()
    try {
        println(result.await())
    } catch (th: Throwable) {
        println("Ooops")
    }
}
