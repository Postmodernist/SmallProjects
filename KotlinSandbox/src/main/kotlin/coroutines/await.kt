package coroutines

import kotlinx.coroutines.*
import kotlin.concurrent.thread

class Task {
    fun jobAsync(): Deferred<String> {
        return GlobalScope.async {
            delay(2000)
            "Job done"
        }
    }
}

fun main() {
    val worker = thread {
        println("Await async result")
        val result = runBlocking { Task().jobAsync().await() }
        println("Received result: $result")
    }
    worker.join()
}
