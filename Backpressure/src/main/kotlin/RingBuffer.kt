import java.io.File
import java.io.RandomAccessFile

/**
 * File ring buffer for [String] elements.
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
        private const val ELEMENT_STRING_SIZE = 2L
        private const val CAPACITY = 3

        private const val FILE_HEADER_SIZE = HEAD.length + 12L  // head + version + elements size + capacity
        private const val ELEMENT_SIZE = ELEMENT_STRING_SIZE + 2  // header + string size + terminator
        private const val FILE_SIZE = (FILE_HEADER_SIZE + ELEMENT_SIZE * CAPACITY)

        private const val FIRST_FLAG = 0b10
        private const val VALID_FLAG = 0b01
    }

    private lateinit var buffer: RandomAccessFile
    private var first = 0  // index of the first element
    private var last = 0  // index of the last element

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
            buf.seek(indexToPosition(it))
            if (checkFirst(buf.readByte())) {
                if (firstElementIndex != -1) {
                    println("$TAG $errorPrefix Multiple first elements found")
                    return false
                }
                firstElementIndex = it
            }
        }
        if (firstElementIndex == -1) {
            println("$TAG $errorPrefix First element not found")
            return false
        }

        // Validate buffer consistency
        var cursor = indexToPosition(firstElementIndex)
        var len: Long = 0
        while (true) {
            buf.seek(cursor)
            cursor = incrementCursor(cursor)
            if (checkValid(buf.readByte()) && positionToIndex(cursor) != firstElementIndex) len++ else break
        }
        while (positionToIndex(cursor) != firstElementIndex) {
            buf.seek(cursor)
            cursor = incrementCursor(cursor)
            if (checkValid(buf.readByte())) {
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

        // Locate first element
        first = 0
        buffer.seek(indexToPosition(first))
        while (first < CAPACITY && !checkFirst(buffer.readByte())) {
            buffer.seek(indexToPosition(++first))
        }

        // Locate last element
        last = first
        buffer.seek(indexToPosition(last))
        if (checkValid(buffer.readByte())) {
            do {
                last = nextIndex(last)
                buffer.seek(indexToPosition(last))
            } while (checkValid(buffer.readByte()) && last != first)
        }
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

        first = 0
        last = 0
        buffer.seek(indexToPosition(first))
    }

    private fun markFirst(header: Byte) = header.toInt() or FIRST_FLAG

    private fun unmarkFirst(header: Byte) = header.toInt() and VALID_FLAG

    private fun markValid(header: Byte) = header.toInt() or VALID_FLAG

    private fun unmarkValid(header: Byte) = header.toInt() and FIRST_FLAG

    private fun checkFirst(header: Byte) = header.toInt() and FIRST_FLAG == FIRST_FLAG

    private fun checkValid(header: Byte) = header.toInt() and VALID_FLAG == VALID_FLAG

    private fun nextIndex(index: Int): Int = (index + 1) % CAPACITY

    private fun incrementCursor(cursor: Long): Long = indexToPosition(nextIndex(positionToIndex(cursor)))

    private fun positionToIndex(position: Long): Int =
        ((position - FILE_HEADER_SIZE) / ELEMENT_SIZE).toInt()

    private fun indexToPosition(index: Int): Long =
        FILE_HEADER_SIZE + (index * ELEMENT_SIZE)
}