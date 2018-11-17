import kotlinx.coroutines.delay

/** Mock http service, */
object HttpService {
    private const val TAG = "HttpService"

    suspend fun send(msg: String) {
        Log.i(TAG, "Sending: '$msg'")
        delay(300)  // simulate http request
        Log.i(TAG,  "Delivered: '$msg'")
    }
}