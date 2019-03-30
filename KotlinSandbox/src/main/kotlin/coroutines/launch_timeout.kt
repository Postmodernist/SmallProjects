package coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

typealias SuspendingBlock = suspend CoroutineScope.() -> Unit

data class LaunchT(var block: SuspendingBlock? = null,
                   var blockT: SuspendingBlock? = null,
                   var timeMillis: Long? = null)

fun LaunchT.block(block: SuspendingBlock) {
    this.block = block
}

fun LaunchT.blockT(blockT: SuspendingBlock) {
    this.blockT = blockT
}

fun CoroutineScope.launchT(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        blocks: LaunchT.() -> Unit
): Job {
    val b = LaunchT().apply(blocks)
    return launch(context, start) {
        val coroutine = this.coroutineContext
        launch {
            delay(b.timeMillis!!)
            b.blockT!!(this)
            coroutine.cancel()
        }
        b.block!!(this)
        coroutine.cancel()
    }
}

fun main() {
    runBlocking {
        launchT {
            block {
                println("block start")
                delay(5000)
                println("block end")
            }
            blockT {
                println("blockT")
            }
            timeMillis = 2000
        }
    }
}
