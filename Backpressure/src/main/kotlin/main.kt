import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val logger = Logger(HttpService)
    repeat(50) {
        logger.log("Test message #${it + 1}")
    }
    runBlocking { delay(60 * 1000) }
}
