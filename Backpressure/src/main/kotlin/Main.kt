import datastructures.RingBuffer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
//    testRingBuffer()
    testLog()
}

private fun testRingBuffer() {
    val rb = RingBuffer(File("ringbuffer"), 3)
    rb.add("foo")
    rb.add("bar")
    rb.add("baz")
    rb.add("qux")
    rb.remove()
}

private fun testLog() {
    val logger = Logger(HttpService)
    runBlocking {
        repeat(50) {
            logger.log("Test message A #${it + 1}")
            delay(10)
        }
        delay(20 * 1000)
        repeat(10) {
            logger.log("Test message B #${it + 1}")
            delay(100)
        }
        delay(20 * 1000)
    }
}