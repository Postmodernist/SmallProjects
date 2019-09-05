package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class CacheSync {
    val data = ArrayList<Long>()

    @Synchronized
    fun add(entry: Long) {
        data.add(entry)
    }
}

fun testThreads() {
    val cache = CacheSync()

    val threads = (0..3).map {
        thread {
            repeat(1000) {
                cache.add(System.currentTimeMillis())
            }
        }
    }

    threads.forEach { it.join() }

    var inOrder = true
    for (i in 0 until cache.data.size - 1) {
        if (cache.data[i] > cache.data[i + 1]) {
            inOrder = false
            break
        }
    }
    println("size = ${cache.data.size}, inOrder = $inOrder")
}

fun testCoroutines() {
    runBlocking {
        val cache = CacheSync()
        val scope = CoroutineScope(Executors.newFixedThreadPool(4).asCoroutineDispatcher())
        val jobs = (0..999).map {
            scope.launch {
                repeat(1000) {
                    cache.add(System.currentTimeMillis())
                }
            }
        }

        jobs.forEach { it.join() }
        scope.cancel()

        var inOrder = true
        for (i in 0 until cache.data.size - 1) {
            if (cache.data[i] > cache.data[i + 1]) {
                inOrder = false
                break
            }
        }
        println("size = ${cache.data.size}, inOrder = $inOrder")
    }
}

fun main() {
    testThreads()
    testCoroutines()
}