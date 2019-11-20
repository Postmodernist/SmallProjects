package core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import core.Table.Request.*

class Table {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val requestChannel = Channel<Request>(BUFFERED)
    private val forks = List<ArrayList<CompletableDeferred<Unit>>>(FORKS_COUNT) { ArrayList() }

    init {
        scope.launch { requestLoop() }
    }

    private suspend fun requestLoop() {
        for (request in requestChannel) {
            when (request) {
                is GetFork -> getFork(request)
                is ReturnFork -> returnFork(request)
            }
        }
    }

    private fun getFork(request: GetFork) {
        val num = request.forkNumber % FORKS_COUNT
        if (forks[num].isEmpty()) {
            request.forkPromise.complete(Unit)
        }
        forks[num].add(request.forkPromise)
    }

    private fun returnFork(request: ReturnFork) {
        val num = request.forkNumber % FORKS_COUNT
        forks[num].removeAt(0)
        if (forks[num].isNotEmpty()) {
            forks[num].first().complete(Unit)
        }
    }

    fun getForkAsync(forkNumber: Int): Deferred<Unit> {
        val forkPromise = CompletableDeferred<Unit>()
        requestChannel.offer(GetFork(forkNumber, forkPromise))
        return forkPromise
    }

    fun returnFork(forkNumber: Int) {
        requestChannel.offer(ReturnFork(forkNumber))
    }

    private sealed class Request {
        class GetFork(val forkNumber: Int, val forkPromise: CompletableDeferred<Unit>) : Request()
        class ReturnFork(val forkNumber: Int) : Request()
    }

    companion object {
        private const val FORKS_COUNT = 5
    }
}