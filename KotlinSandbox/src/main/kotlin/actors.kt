import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach

// Actor mailbox message types
private sealed class Doable {
    data class Spin(val id: Int) : Doable()
    data class Done(val ack: CompletableDeferred<Boolean>) : Doable()
}

// Process actor messages on the CommonPool thread pool
@ObsoleteCoroutinesApi
private val actor = GlobalScope.actor<Doable>(capacity = 0) {
    consumeEach { doable ->
        when (doable) {
            is Doable.Spin -> spin(doable.id)
        }
    }
}

/** Spin for at least [durationMillis] period of time. */
private fun spin(value: Int, durationMillis: Int = 10): Int {
    val startMillis = System.currentTimeMillis()
    while (System.currentTimeMillis() - startMillis < durationMillis) {
        // Simulate heavy computation
    }
    println("[thread=${Thread.currentThread().name}] [$value] processed")
    return value
}

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    val start = System.currentTimeMillis()
    runBlocking {
        (1..100).map { Doable.Spin(it) }
                .forEach { s ->
                    launch(Dispatchers.IO) {
                        actor.send(s)
                        val duration = System.currentTimeMillis() - start
                        println("[thread=${Thread.currentThread().name}] [${s.id}] time=$duration")
                    }
                }
    }
    println("time=${System.currentTimeMillis() - start}")
}

