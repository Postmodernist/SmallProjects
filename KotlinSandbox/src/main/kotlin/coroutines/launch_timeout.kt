package coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

typealias SuspendingBlock = suspend CoroutineScope.() -> Unit

data class LaunchT(var block: SuspendingBlock = {},
                   var blockT: SuspendingBlock = {},
                   var timeMillis: Long = Long.MAX_VALUE)

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
): Job = launch(context, start) {
    val coroutine = this.coroutineContext
    val t = LaunchT().apply(blocks)
    launch {
        delay(t.timeMillis)
        (t.blockT)()
        coroutine.cancel()
    }
    (t.block)()
    coroutine.cancel()
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
