package coroutines

import kotlinx.coroutines.*

object Dispatcher {
    fun doHardWork(trigger: CompletableDeferred<Boolean>) =
            GlobalScope.launch {
                println("Hard work started...")
                delay(3000)
                println("Hard work finished.")
                trigger.complete(true)
            }
}

object Client {
    val trigger = CompletableDeferred<Boolean>()

    suspend fun waitForTrigger() {
        trigger.await()
        println("Trigger fired!")
    }
}

fun main() {

    Dispatcher.doHardWork(Client.trigger)

    runBlocking {
        Client.waitForTrigger()
    }
}
