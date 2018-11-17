class Logger(httpService: HttpService) {
    private val bufferCapacity = 25

    private val ramBuffer = RamBuffer(bufferCapacity) { fileBuffer.add(it) }
    private val fileBuffer = FileBuffer(bufferCapacity, httpService::send)

    fun log(msg: String) {
        Log.i("Logger", "Received: '$msg'")
        synchronized(ramBuffer) {
            ramBuffer.add(msg)
        }
    }
}
