import kotlinx.coroutines.delay

/** Mock http service, */
object HttpService {
    suspend fun send(msg: String) {
        Log.i("HttpService", "Sending: '$msg'")
        delay(300)  // simulate http request
        Log.i("HttpService",  "Delivered: '$msg'")
    }
}