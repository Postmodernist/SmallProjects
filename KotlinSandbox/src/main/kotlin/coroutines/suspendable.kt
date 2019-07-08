package coroutines

import kotlinx.coroutines.*
import kotlin.math.atan2
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class SuspendableCycle {

    var i: Int = 0

    suspend fun run() {
        repeat(100) {
            i++
            for (i in 0..500_000) atan2(Random.nextDouble(), Random.nextDouble())
            yield()
        }
    }
}

fun main() {
    val cycle = SuspendableCycle()
    val t = runBlocking {
        measureTimeMillis {
            val c = launch { cycle.run() }
            delay(1000)
            c.cancel()
        }
    }
    println("cycles = ${cycle.i}, time = $t")
}
