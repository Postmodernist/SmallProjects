package coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@FlowPreview
fun main() = runBlocking {

    val ints: Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            emit(i)
        }
    }

    // Single coroutine (sequential)
    val t1 = measureTimeMillis {
        ints.collect {
            delay(100)
            println(it) }
    }
    println("Collected in $t1 ms")

    // Two communicating coroutines (parallel)
    val t2 = measureTimeMillis {
        ints.buffer().collect {
            delay(100)
            println(it) }
    }
    println("Collected in $t2 ms")

}

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@FlowPreview
fun <T> Flow<T>.buffer(size: Int = 0): Flow<T> = flow {
    coroutineScope {
        val channel = produce(capacity = size) {
            collect { send(it) }
        }
        channel.consumeEach { emit(it) }
    }
}
