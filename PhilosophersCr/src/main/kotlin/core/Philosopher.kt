package core

import core.Philosopher.State.*
import kotlinx.coroutines.delay
import kotlin.random.Random

class Philosopher(
    id: Int,
    private val name: String,
    private val table: Table
) {
    private val firstFork = id + (id and 1)
    private val secondFork = id + 1 - (id and 1)
    private var state = THINK
    private var eatCount = 0

    suspend fun live() {
        while (true) {
            state = THINK
            delay(randomInterval())
            state = HUNGRY
            table.getForkAsync(firstFork).await()
            table.getForkAsync(secondFork).await()
            state = EAT
            delay(randomInterval())
            eatCount++
            table.returnFork(firstFork)
            table.returnFork(secondFork)
        }
    }

    private fun randomInterval() = Random.nextLong(1, 100)

    fun state() = "[" +
            String.format("%-3s", name) +
            String.format("%-7s", state) +
            String.format("%4d", eatCount) +
            "]"

    private enum class State { THINK, HUNGRY, EAT }
}