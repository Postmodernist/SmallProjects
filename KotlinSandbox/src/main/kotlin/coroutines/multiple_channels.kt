package coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.selects.select
import kotlin.random.Random

class DataSink {
    val intChannel = Channel<Data<Int>>(BUFFERED)
    val stringChannel = Channel<Data<String>>(BUFFERED)

    val eventLoop = CoroutineScope(Dispatchers.Default).launch {
        eventLoop(intChannel, stringChannel)
    }

    private suspend fun eventLoop(
            intSource: ReceiveChannel<Data<Int>>,
            stringSource: ReceiveChannel<Data<String>>
    ) {
        var intSourceOpen = true
        var stringSourceOpen = true
        while (intSourceOpen || stringSourceOpen) {
            select<Unit> {
                stringSource.onReceive { stringSourceOpen = receive(it) }
                intSource.onReceive { intSourceOpen = receive(it) }
            }
        }
    }

    private suspend fun <T> receive(data: Data<T>): Boolean {
        return when (data) {
            is Data.Value -> {
                println("received ${data.value}")
                delay(Random.nextLong(100, 400))
                true
            }
            is Data.Close -> {
                false
            }
        }
    }
}

sealed class Data<T> {
    class Value<T>(val value: T) : Data<T>()
    class Close<T> : Data<T>()
}

fun main() {
    runBlocking {
        val dataSink = DataSink()
        listOf(
                generate("INT", 10, 4.0, dataSink.intChannel) {
                    Random.nextInt(100, 1000)
                },
                generate("STR", 5, 1.5, dataSink.stringChannel) {
                    randomString()
                }
        ).joinAll()
        dataSink.eventLoop.join()
    }
}

private fun <T> CoroutineScope.generate(
        name: String,
        n: Int,
        rate: Double,
        sink: SendChannel<Data<T>>,
        supplier: () -> T
): Job {
    return launch {
        repeat(n) {
            delay((1000 / rate).toLong())
            val x = supplier()
            println("$name sending $x")
            sink.send(Data.Value(x))
        }
        sink.send(Data.Close())
        println("$name finished")
    }
}

private fun randomString(): String {
    val sb = StringBuilder()
    repeat(3) {
        sb.append(Random.nextInt('a'.toInt(), 'z'.toInt()).toChar())
    }
    return sb.toString()
}