package coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    // create a channel that produces numbers from 1 to 3 with 200ms delays between them
    val source = produce {
        println("Begin") // mark the beginning of this coroutine in output
        for (x in 1..3) {
            delay(200) // wait for 200ms
            send(x) // send number x to the channel
        }
    }
    // print elements from the source
    println("Elements:")
    source.consumeEach { // consume elements from it
        println(it)
    }
    // print elements from the source AGAIN
    println("Again:")
    source.consumeEach { // consume elements from it
        println(it)
    }
}
