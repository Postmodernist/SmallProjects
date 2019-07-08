package coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Enclosing CoroutineScope prevents leaks by auto cancelling all child coroutines in case
 * of error or external cancellation. If child coroutines were launched in GlobalScope instead
 * they would leak.
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
private suspend fun processReferences(refs: List<String>) = coroutineScope {
    for (ref in refs) {
        // Process each reference in separate coroutine.
        launchRefProcessing(name = CoroutineName(ref)) {
            println("Processing '$ref' started.")
            delay(2000)  // simulate long task
            println("Processing '$ref' finished.")
        }
    }
}

/** This coroutine builder is similar to [launch] but returns coroutine that reports cancellation. */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
private fun CoroutineScope.launchRefProcessing(
        context: CoroutineContext = EmptyCoroutineContext,
        name: CoroutineName = CoroutineName("Unknown reference"),
        block: suspend CoroutineContext.() -> Unit
): Job {
    val newContext = newCoroutineContext(context) + name
    val coroutine = object : AbstractCoroutine<Unit>(newContext, true) {
        override fun onCancelled(cause: Throwable, handled: Boolean) {
            super.onCancelled(cause, handled)
            println("Processing '${coroutineContext[CoroutineName]?.name}' cancelled!")
        }
    }
    coroutine.start(CoroutineStart.DEFAULT, coroutine, block)
    return coroutine
}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun main() = runBlocking {
    // Abort processing after a timeout
    withTimeout(1000) {
        processReferences(listOf("Reference1", "Reference2", "Reference3"))
    }
}
