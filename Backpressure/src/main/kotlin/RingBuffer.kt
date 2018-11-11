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
class RingBuffer(private val path: String = "ringbuffer") {
    companion object {
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
        makeFile()
    }

    private fun makeFile() {
        with(File(path)) {
            if (exists()) {
                delete()
            }
            buffer = RandomAccessFile(this, "rwd")
        }

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
}