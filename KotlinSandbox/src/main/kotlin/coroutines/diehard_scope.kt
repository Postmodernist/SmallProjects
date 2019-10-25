package coroutines

import kotlinx.coroutines.*

class Supervisor {

    private val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        val id = coroutineContext[CoroutineName.Key]?.name
        println("[$id] $throwable")
    }

    private var scope: CoroutineScope = newScope()

    fun submit(id: Int, block: suspend CoroutineScope.() -> Unit): Job =
            scope.launch(CoroutineName(id.toString())) { block() }

    private fun newScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)
}

class Diehard {

    private val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        val id = coroutineContext[CoroutineName.Key]?.name
        println("scope died: [$id] $throwable")
        scope = newScope()
    }

    private var scope: CoroutineScope = newScope()

    fun submit(id: Int, block: suspend CoroutineScope.() -> Unit): Job =
            scope.launch(CoroutineName(id.toString())) { block() }

    private fun newScope(): CoroutineScope =
            CoroutineScope(Job() + Dispatchers.Default + handler)
}

fun main() {
    val supervisor = Supervisor()
    runBlocking {
        val jobs = listOf(
                supervisor.submit(1) {
                    println("[1] start")
                    delay(100)
                    println("[1] finish")
                },
                supervisor.submit(2) {
                    println("[2] start")
                    delay(50)
                    error("bang!")
                },
                supervisor.submit(3) {
                    println("[3] start")
                    delay(100)
                    println("[3] finish")
                }
        )
        jobs.joinAll()
        val diehard = Diehard()
        diehard.submit(4) {
            println("[4] start")
            delay(100)
            println("[4] finish")
        }.join()
        diehard.submit(5) {
            println("[5] start")
            delay(50)
            error("bang!")
        }.join()
        diehard.submit(6) {
            println("[6] start")
            delay(100)
            println("[6] finish")
        }.join()
    }
}
