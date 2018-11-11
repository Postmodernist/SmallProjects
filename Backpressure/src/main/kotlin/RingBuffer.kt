import java.io.File
import java.io.RandomAccessFile

/**
 * Ring buffer backed by file.
 *
 * File header format:
 * [3 bytes] Head
 * [4 bytes] Version
 * [4 bytes] Element size (Bytes)
 * [4 bytes] Capacity (Elements)
 *
 * Element header format:
 * [1 byte] bit#0 -- validity, bit#1 -- first element
 *
 * Element content is a zero-terminated character string.
 */
class RingBuffer(path: String = "ringbuffer") {
    companion object {
        private const val TAG = "[RingBuffer]"

        private const val HEAD = "RBF "
        private const val VERSION = 1
        private const val ELEMENT_STRING_SIZE = 1022L
        private const val CAPACITY = 25

        private const val FILE_HEADER_SIZE = HEAD.length + 12L  // head + version + elements size + capacity
        private const val ELEMENT_SIZE = ELEMENT_STRING_SIZE + 2  // header + string size + terminator
        private const val FILE_SIZE = (FILE_HEADER_SIZE + ELEMENT_SIZE * CAPACITY)

        private const val FIRST_FLAG = 0b10
        private const val VALID_FLAG = 0b01
    }

    private lateinit var buffer: RandomAccessFile

    init {
        val bufferFile = File(path)
        if (validateFile(bufferFile)) {
            openFile(bufferFile)
        } else {
            newFile(bufferFile)
        }
    }

    fun onDestroy() {
        buffer.close()
    }

    private fun validateFile(bufferFile: File): Boolean {
        if (!bufferFile.exists() || bufferFile.isDirectory) {
            return false
        }

        val buf = RandomAccessFile(bufferFile, "r")
        val errorPrefix = "Buffer validation failed:"

        // Validate file header
        val headBytes = ByteArray(HEAD.length)
        buf.read(headBytes)
        val head = String(headBytes)
        if (HEAD != head) {
            println("$TAG $errorPrefix Invalid header (expected '$HEAD', found '$head')")
            return false
        }
        val version = buf.readInt()
        if (VERSION != version) {
            println("$TAG $errorPrefix Version mismatch (expected $VERSION, found $version)")
            return false
        }
        val elementSize = buf.readInt().toLong()
        if (ELEMENT_SIZE != elementSize) {
            println("$TAG $errorPrefix Wrong element size (expected $ELEMENT_SIZE, found $elementSize)")
            return false
        }
        val capacity = buf.readInt()
        if (CAPACITY != capacity) {
            println("$TAG $errorPrefix Wrong capacity (expected $CAPACITY, found $capacity)")
            return false
        }

        // Validate file size
        val fileSize = bufferFile.length()
        if (FILE_SIZE != fileSize) {
            println("$TAG $errorPrefix Wrong file size (expected $FILE_SIZE, found $fileSize)")
            return false
        }

        // Validate first element
        var firstElementIndex = -1
        repeat(capacity) {
            buf.seek(indexToPosition(it, true))
            val header = buf.readByte().toInt()
            if (checkFirst(header)) {
                if (firstElementIndex != -1) {
                    println("$TAG $errorPrefix Multiple first elements found")
                    return false
                }
                firstElementIndex = it
            }
        }

        // Validate buffer consistency
        var cursor = indexToPosition(firstElementIndex, true)
        var len: Long = 0
        while (true) {
            buf.seek(cursor)
            cursor = incrementCursor(cursor, true)
            val header = buf.readByte().toInt()
            if (checkValid(header) && positionToIndex(cursor, true) != firstElementIndex) len++ else break
        }
        while (positionToIndex(cursor, true) != firstElementIndex) {
            buf.seek(cursor)
            cursor = incrementCursor(cursor, true)
            val header = buf.readByte().toInt()
            if (checkValid(header)) {
                println("$TAG $errorPrefix Buffer is inconsistent")
                return false
            }
        }

        println("$TAG Validation passed")
        return true
    }

    private fun openFile(bufferFile: File) {
        println("$TAG Restoring buffer")
        buffer = RandomAccessFile(bufferFile, "rwd")
    }

    private fun newFile(bufferFile: File) {
        println("$TAG Creating new buffer")
        if (bufferFile.exists()) {
            bufferFile.delete()
        }
        buffer = RandomAccessFile(bufferFile, "rwd")

        // Allocate space
        buffer.seek(FILE_SIZE - 1)
        buffer.writeByte(0)
        buffer.seek(0)

        // Write header
        buffer.writeBytes(HEAD)
        buffer.writeInt(VERSION)
        buffer.writeInt(ELEMENT_SIZE.toInt())
        buffer.writeInt(CAPACITY)

        // Write first element header
        buffer.seek(FILE_HEADER_SIZE)
        buffer.writeByte(FIRST_FLAG)
        buffer.seek(FILE_HEADER_SIZE)
    }

    private fun markFirst(header: Int) = header or FIRST_FLAG

    private fun unmarkFirst(header: Int) = header and VALID_FLAG

    private fun markValid(header: Int) = header or VALID_FLAG

    private fun unmarkValid(header: Int) = header and FIRST_FLAG

    private fun checkFirst(header: Int) = header and FIRST_FLAG == FIRST_FLAG

    private fun checkValid(header: Int) = header and VALID_FLAG == VALID_FLAG

    private fun incrementCursor(cursor: Long, absolute: Boolean = false): Long =
        indexToPosition((positionToIndex(cursor, absolute) + 1) % CAPACITY, absolute)

    private fun positionToIndex(position: Long, absolute: Boolean = false): Int =
        ((if (absolute) position - FILE_HEADER_SIZE else position) / ELEMENT_SIZE).toInt()

    private fun indexToPosition(index: Int, absolute: Boolean = false): Long =
        (index * ELEMENT_SIZE) + (if (absolute) FILE_HEADER_SIZE else 0)
}