import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    testRingBuffer()
//    testLog()
}

private fun testRingBuffer() {
    val rb = RingBuffer()
    rb.add("a")
    rb.add("b")
    println(rb.size)
    rb.add("c")
    println(rb.remove())
    println(rb.remove())
    println(rb.size)
    println(rb.remove())
    rb.onDestroy()
}

private fun testLog() {
    val logger = Logger(HttpService)
    runBlocking {
        repeat(50) {
            logger.log("Test message #${it + 1}")
            delay(10)
        }
        delay(60 * 1000)
    }
}