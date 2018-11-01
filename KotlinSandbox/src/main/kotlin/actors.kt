import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach

// Actor mailbox message types
private sealed class Doable {
    data class Spin(val id: Int) : Doable()
    data class Done(val ack: CompletableDeferred<Boolean>) : Doable()
}

@ObsoleteCoroutinesApi
private val children: List<SendChannel<Doable>> = Array(5) { createSpinActor() }.asList()
private var next = 0

// Process actor messages
@ObsoleteCoroutinesApi
private val actor = GlobalScope.actor<Doable>(Dispatchers.Default, capacity = 0) {
    consumeEach { doable ->
        when (doable) {
            is Doable.Spin -> children[next++ % children.size].send(doable)
            is Doable.Done -> {
                val acks = (1..children.size).map { CompletableDeferred<Boolean>() }
                acks.forEachIndexed { index, ack -> children[index].send(Doable.Done(ack)) }
                acks.map { it.await() }
                doable.ack.complete(true)
            }
        }
    }
}

@ObsoleteCoroutinesApi
private fun createSpinActor() = GlobalScope.actor<Doable>(Dispatchers.Default, capacity = 0) {
    consumeEach { doable ->
        when (doable) {
            is Doable.Spin -> spin(doable.id)
            is Doable.Done -> doable.ack.complete(true)
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
fun main(args: Array<String>) = runBlocking {
    val start = System.currentTimeMillis()
    val ack = CompletableDeferred<Boolean>()
    (1..101).map { if (it == 101) Doable.Done(ack) else Doable.Spin(it) }
            .forEach { s ->
                println("[thread=${Thread.currentThread().name}] [$s] before")
                actor.send(s)
                val duration = System.currentTimeMillis() - start
                println("[thread=${Thread.currentThread().name}] [$s] time=$duration")
            }
    // Wait for the Actor to trigger completion of the Done operation
    ack.await()
    println("time=${System.currentTimeMillis() - start}")
}
