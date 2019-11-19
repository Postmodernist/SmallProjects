package coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.selects.select
import kotlin.random.Random

class DataSink {
    val intChannel = Channel<Int>(BUFFERED)
    val stringChannel = Channel<String>(BUFFERED)
    val eventLoop = CoroutineScope(Dispatchers.Default).launch {
        withTimeout(1000) {
            eventLoop(intChannel, stringChannel)
        }
    }

    private suspend fun eventLoop(
            intSource: ReceiveChannel<Int>,
            stringSource: ReceiveChannel<String>
    ) {
        while (true) {
            select<Unit> {
                stringSource.onReceive { println("received string: $it"); delay(200) }
                intSource.onReceive { println("received int: $it"); delay(200) }
            }
        }
    }
}

fun main() {
    runBlocking {
        val dataSink = DataSink()
        val jobs = listOf(
                generateInts(dataSink.intChannel),
                generateStrings(dataSink.stringChannel)
        )
        jobs.joinAll()
        dataSink.eventLoop.join()
    }
}

private fun CoroutineScope.generateInts(sink: SendChannel<Int>): Job {
    return launch {
        repeat(10) {
            delay(Random.nextLong(300))
            val x = Random.nextInt(1000)
            println("sending: $x")
            sink.send(x)
        }
        println("generateInts finished")
    }
}

private fun CoroutineScope.generateStrings(sink: SendChannel<String>): Job {
    return launch {
        repeat(5) {
            delay(Random.nextLong(1000))
            val s = randomString()
            println("sending: $s")
            sink.send(s)
        }
        println("generateStrings finished")
    }
}

fun randomString(): String {
    val sb = StringBuilder()
    repeat(Random.nextInt(4, 10)) {
        sb.append(Random.nextInt('a'.toInt(), 'z'.toInt()).toChar())
    }
    return sb.toString()
}
