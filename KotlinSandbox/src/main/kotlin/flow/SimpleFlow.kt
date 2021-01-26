package flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

fun main() {
    val counter: Flow<Int> = flow {
        repeat(10) {
            emit(it)
            delay(100)
        }
    }

    runBlocking {
        println(counter.count())
    }
}