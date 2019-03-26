package coroutines

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private fun sendWithChannels() = runBlocking {
    val channel = Channel<Int>()
    launch {
        repeat(10) {
            delay(100)
            channel.send(it)
        }
        channel.close()
    }
    launch {
        for (i in channel) {
            println(i)
        }
    }
}

@ObsoleteCoroutinesApi
private fun sendWithActors() = runBlocking {
    val actor = actor<Int> {
        for (i in channel) {
            println(i)
        }
    }
    launch {
        repeat(10) {
            delay(100)
            actor.send(it)
        }
        actor.close()
    }
}

@ObsoleteCoroutinesApi
fun main() {
    sendWithChannels()
    sendWithActors()
}
