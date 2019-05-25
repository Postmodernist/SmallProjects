package datastructs

import datastructs.HeapCommand.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val commands = makeCommandsQueue()

    val task1 = GlobalScope.launch {
        val ds = BinaryHeap<Int>()
        val t = measureTimeMillis {
            for (command in commands) {
                when (command) {
                    is Insert -> ds.insert(command.element)
                    is Extract -> ds.extract()
                    is Remove -> ds.remove(command.element)
                }
            }
        }
        println("[${Thread.currentThread().name}] BinaryHeap finished, t = ${t}ms")
    }

    val task2 = GlobalScope.launch {
        val ds = PriorityQueue<Int>()
        val t = measureTimeMillis {
            for (command in commands) {
                when (command) {
                    is Insert -> ds.offer(command.element)
                    is Extract -> ds.poll()
                    is Remove -> ds.remove(command.element)
                }
            }
        }
        println("[${Thread.currentThread().name}] PriorityQueue finished, t = ${t}ms")
    }

    val task3 = GlobalScope.launch {
        val ds = FibonacciHeap<Int>()
        val t = measureTimeMillis {
            for (command in commands) {
                when (command) {
                    is Insert -> ds.insert(command.element)
                    is Extract -> ds.extract()
                    is Remove -> ds.remove(command.element)
                }
            }
        }
        println("[${Thread.currentThread().name}] FibonacciHeap finished, t = ${t}ms")
    }

    runBlocking {
        task1.join()
        task2.join()
        task3.join()
    }
}

private fun makeCommandsQueue(): Array<HeapCommand> {
    val elements = ArrayList<Int>()
    return Array(1_000_000) { i ->
        if (i < 1000) {
            val element = Random.nextInt(1_000_000)
            elements.add(element)
            Insert(element)
        } else {
            when (if (elements.isEmpty()) 0 else Random.nextInt(3)) {
                0 -> {
                    val element = Random.nextInt(1_000_000)
                    elements.add(element)
                    Insert(element)
                }
                1 -> {
                    val element = elements.min()!!
                    elements.remove(element)
                    Extract
                }
                2 -> {
                    val j = Random.nextInt(elements.size)
                    val element = elements[j]
                    elements.removeAt(j)
                    Remove(element)
                }
                else -> throw RuntimeException("Wtf?")
            }
        }
    }
}

sealed class HeapCommand {
    data class Insert(val element: Int) : HeapCommand()
    data class Remove(val element: Int) : HeapCommand()
    object Extract : HeapCommand()
}
