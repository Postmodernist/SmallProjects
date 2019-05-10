package datastructs

import datastructs.Command.*
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
                    is Remove -> ds.remove(command.element)
                    is Extract -> ds.extract()
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
                    is Remove -> ds.remove(command.element)
                    is Extract -> ds.poll()
                }
            }
        }
        println("[${Thread.currentThread().name}] PriorityQueue finished, t = ${t}ms")
    }

    runBlocking {
        task1.join()
        task2.join()
    }
}

private fun makeCommandsQueue(): Array<Command> {
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
                    val j = Random.nextInt(elements.size)
                    val element = elements[j]
                    elements.removeAt(j)
                    Remove(element)
                }
                2 -> {
                    val element = elements.min()!!
                    elements.remove(element)
                    Extract
                }
                else -> throw RuntimeException("Wtf?")
            }
        }
    }
}

sealed class Command {
    data class Insert(val element: Int) : Command()
    data class Remove(val element: Int) : Command()
    object Extract : Command()
}
