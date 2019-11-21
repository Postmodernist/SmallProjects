import core.Philosopher
import core.Table
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

fun main() = runBlocking {
    runPhilosophers(Table())
}

private suspend fun runPhilosophers(table: Table) {
    coroutineScope {
        val names = listOf("A", "B", "C", "D", "E")
        val philosophers = List(names.size) { Philosopher(it, names[it], table) }
        philosophers.forEach { launch { it.live() } }
        launch { logStates(philosophers) }
    }
}

private suspend fun logStates(philosophers: List<Philosopher>) {
    while (true) {
        delay(TimeUnit.SECONDS.toMillis(1))
        val states = philosophers.joinToString("  ") { it.state() }
        println(states)
    }
}