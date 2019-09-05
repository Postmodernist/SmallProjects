package coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.measureTimeMillis

suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100  // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        coroutineScope {
            // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
    println("Completed ${n * k} actions in $time ms")
}

fun main() = runBlocking {
    var counter = 0
    val lock = Mutex()
    withContext(Dispatchers.Default) {
        massiveRun {
            lock.withLock {
                counter++
            }
        }
    }
    println("Counter = $counter")
}
