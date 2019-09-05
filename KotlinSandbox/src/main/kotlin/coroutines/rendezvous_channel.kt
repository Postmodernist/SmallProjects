package coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val channel = Channel<Int>()

fun main() {
    runBlocking {
        val sender = GlobalScope.launch {
            var i = 0
            while (true) {
                println("sending $i")
                channel.send(i++)
                delay(500)
            }
        }
        delay(2000)
        for (i in channel) {
            println(i)
            break
        }
        sender.cancel()
    }
}