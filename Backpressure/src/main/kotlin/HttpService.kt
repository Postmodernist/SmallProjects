import kotlinx.coroutines.delay

/** Mock http service, */
object HttpService {
    suspend fun send(msg: String) {
        println("[HttpService] Sending: \"$msg\"")
        delay(300)  // simulate long request
        println("[HttpService] Delivered: \"$msg\"")
    }
}