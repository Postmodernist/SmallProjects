package datastructs

import datastructs.RedBlackTreeCommand.Insert
import datastructs.RedBlackTreeCommand.Remove
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val commands = makeCommandsQueue()

    val task1 = GlobalScope.launch {
        val ds = TreeSet<Int>()
        val t = measureTimeMillis {
            for (command in commands) {
                when (command) {
                    is Insert -> ds.add(command.element)
                    is Remove -> ds.remove(command.element)
                }
            }
        }
        println("[${Thread.currentThread().name}] TreeSet finished, t = ${t}ms")
    }

    val task2 = GlobalScope.launch {
        val ds = RedBlackTree<Int>()
        val t = measureTimeMillis {
            for (command in commands) {
                when (command) {
                    is Insert -> ds.insert(command.element)
                    is Remove -> ds.remove(command.element)
                }
            }
        }
        println("[${Thread.currentThread().name}] RedBlackTree finished, t = ${t}ms")
    }

    runBlocking {
        task1.join()
        task2.join()
    }
}

private fun makeCommandsQueue(): Array<RedBlackTreeCommand> {
    val a = ArrayList<Int>()
    return Array(1_000_000) { i ->
        if (i < 1000) {
            val k = Random.nextInt()
            a.add(k)
            Insert(k)
        } else {
            if (a.isEmpty() || Random.nextBoolean()) {
                val k = Random.nextInt()
                a.add(k)
                Insert(k)
            } else {
                val j = Random.nextInt(a.size)
                val k = a[j]
                a.removeAt(j)
                Remove(k)
            }
        }
    }
}

sealed class RedBlackTreeCommand {
    data class Insert(val element: Int) : RedBlackTreeCommand()
    data class Remove(val element: Int) : RedBlackTreeCommand()
}
