package flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.system.measureTimeMillis

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalCoroutinesApi
class FlowTests {

    @Test
    fun skip_delay_in_flow() {
        val f: Flow<Int> = flow {
            repeat(10) {
                emit(it)
                delay(100)
            }
        }

        val t = measureTimeMillis {
            runBlockingTest {
                f.count()
            }
        }

        println("Finished in $t ms")
    }

    class ArticleViewModel(val dispatcher: CoroutineDispatcher) {
        private var likeCount = 0

        suspend fun getLikeCountFromDb(): Int =
            withContext(dispatcher) {
                delay(10_000) // Assume we get data from database
                likeCount
            }
    }

    @Test
    fun skip_delay_with_context() {
        val testDispatcher = TestCoroutineDispatcher()
        val avm = ArticleViewModel(testDispatcher)
        testDispatcher.runBlockingTest {
            val t = measureTimeMillis {
                val likeCount = avm.getLikeCountFromDb()
                assertEquals(0, likeCount)
            }
            println("Finished in $t ms")
        }
    }

    fun CoroutineScope.updateResult(result: ArrayList<String>) {
        launch {
            delay(1000)
            result.add("hello")
        }
    }

    @Test
    fun eager_launch() {
        val result = ArrayList<String>()

        runBlockingTest {
            updateResult(result)
        }

        assertEquals(listOf("hello"), result)
    }

    fun CoroutineScope.waitForResult(result: Deferred<String>) {
        launch {
            withTimeout(1000) {
                result.await()
            }
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun advance_time() {
        // fails, but should work according to
        // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/
        runBlockingTest {
            val endless = CompletableDeferred<String>()
            waitForResult(endless)
            advanceTimeBy(2000)
        }
    }

    fun CoroutineScope.printWithDelay() {
        launch {
            println(1)   // executes after runCurrent() is called
            delay(1_000) // suspends until time is advanced by at least 1_000
            println(2)   // executes after advanceTimeBy(1_000)
        }
    }

    @Test
    fun pause_dispatcher() = runBlockingTest {
        pauseDispatcher {
            printWithDelay()
            // the coroutine started by foo has not run yet
            runCurrent() // the coroutine started by foo advances to delay(1_000)
            // the coroutine started by foo has called println(1), and is suspended on delay(1_000)
            advanceTimeBy(1_000) // progress time, this will cause the delay to resume
            // the coroutine started by foo has called println(2) and has completed here
        }
    }

    @Test
    fun advance_until_idle() {
        val scope = TestCoroutineScope()
        scope.printWithDelay()
        scope.advanceUntilIdle()
    }

}