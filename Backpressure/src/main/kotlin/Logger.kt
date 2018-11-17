class Logger(httpService: HttpService) {
    companion object {
        private const val TAG = "Logger"
    }

    private val bufferCapacity = 25
    private val ramBuffer = RamBuffer(bufferCapacity) { fileBuffer.add(it) }
    private val fileBuffer = FileBuffer(bufferCapacity, httpService::send)

    fun log(msg: String) {
        Log.i(TAG, "Received: '$msg'")
        synchronized(ramBuffer) {
            ramBuffer.add(msg)
        }
    }
}
