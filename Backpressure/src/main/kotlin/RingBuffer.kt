import java.io.File
import java.io.RandomAccessFile
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * File ring buffer for [String] elements.
 *
 * File header format:
 * [4 bytes] Head
 * [4 bytes] Version
 * [8 bytes] Global index
 * [4 bytes] Element size (Bytes)
 * [4 bytes] Capacity (Elements)
 *
 * Element header format:
 * [1 byte] bit#0 -- validity, bit#1 -- first element
 *
 * Element content string is a zero-terminated character string. It is prepended with its global
 * index converted to string format.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class RingBuffer(file: File = File("ringbuffer"), val capacity: Int = 1000) {
    companion object {
        private const val TAG = "RingBuffer"
        /** File head */
        private const val HEAD = "RBF "
        /** File format version */
        private const val VERSION = 2
        /** Maximum size of the content string */
        private const val CONTENT_STRING_SIZE = 1022
        /** File header: head + version + global index + elements size + capacity */
        private const val FILE_HEADER_SIZE = HEAD.length + 4 + 8 + 4 + 4
        /** Element: header + global index + string + terminator */
        private const val ELEMENT_SIZE = CONTENT_STRING_SIZE + 2
        /** Offset from the beginning of the file to global index field */
        private const val GLOBAL_INDEX_POSITION = 8L

        private const val FIRST_FLAG: Byte = 0b10
        private const val VALID_FLAG: Byte = 0b01
    }

    val size get() = sz

    private val fileSize = (FILE_HEADER_SIZE + ELEMENT_SIZE * capacity).toLong()
    private var buffer: RandomAccessFile = if (validateFile(file)) openFile(file) else newFile(file)
    private var first = 0  // index of the first element
    private var last = 0  // index of the last element
    private var sz = 0  // buffer size
    private var globalIndex = 0L  // global persistent index for the next log message

    /** Closes the buffer file. */
    fun onDestroy() {
        buffer.close()
    }

    /** Removes all of the elements from the buffer. */
    fun clear() {
        // Clear headers
        var cursor = FILE_HEADER_SIZE.toLong()
        buffer.seek(FILE_HEADER_SIZE.toLong())
        buffer.writeByte(FIRST_FLAG.toInt())
        repeat(capacity - 1) {
            cursor = cursor.incrementCursor()
            buffer.seek(cursor)
            buffer.writeByte(0)
        }

        // Reset state
        first = 0
        last = 0
        sz = 0
    }

    /** Inserts a specified element into the buffer, evicting the head element if the buffer is full. */
    fun add(element: String) {
        // Prepend the content string with its global index
        val withIndex = "$globalIndex $element"

        // Truncate and copy element to output array
        val truncated = withIndex.substring(0, withIndex.length.coerceAtMost(CONTENT_STRING_SIZE))
        val output = ByteArray(ELEMENT_SIZE + 1)  // element + header of the next element
        System.arraycopy(truncated.toByteArray(), 0, output, 1, truncated.length)

        // Update headers of current and next element
        if (first == last && sz > 0) {
            // Buffer is full, evicting
            output[0] = VALID_FLAG
            output[ELEMENT_SIZE] = VALID_FLAG or FIRST_FLAG
            first = last.nextIndex()
            sz--
        } else if (first == last && sz == 0) {
            // Buffer is empty
            output[0] = VALID_FLAG or FIRST_FLAG
        } else if (first == last.nextIndex()) {
            // Writing to the last empty slot
            output[0] = VALID_FLAG
            output[ELEMENT_SIZE] = VALID_FLAG or FIRST_FLAG
        } else {
            // Writing to partially filled buffer
            output[0] = VALID_FLAG
        }
        val index = last
        last = last.nextIndex()

        // Write result to file
        buffer.seek(index.indexToPosition())
        if (index == capacity - 1) {
            // Traverse edge
            buffer.write(output.sliceArray(0 until output.size - 1))
            buffer.seek(FILE_HEADER_SIZE.toLong())
            buffer.writeByte(output[ELEMENT_SIZE].toInt())
        } else {
            buffer.write(output)
        }

        sz++
        globalIndex++

        // Persist global index
        buffer.seek(GLOBAL_INDEX_POSITION)
        buffer.writeLong(globalIndex)
    }

    /** Adds all of the elements in the specified collection to the buffer. */
    fun addAll(elements: Collection<String>) {
        if (elements.size > capacity) {
            clear()
            elements.drop(elements.size - capacity).forEach { add(it) }
        } else {
            elements.forEach { add(it) }
        }
    }

    /** Retrieves and removes the head of the buffer. */
    fun remove(): String {
        if (sz == 0) {
            throw NoSuchElementException("Trying to remove from empty buffer")
        }

        val pos = first.indexToPosition()

        // Update current header
        buffer.seek(pos)
        buffer.writeByte(0)

        // Read element
        val input = ByteArray(ELEMENT_SIZE - 1)
        buffer.read(input)

        // Get string length
        val zero: Byte = 0
        var len = 0
        while (input[len] != zero) {
            len++
        }

        // Extract string
        val element = String(input.sliceArray(0 until len))

        // Update header of the next element
        val newHeader = (if (sz == 1) 0 else VALID_FLAG) or FIRST_FLAG
        first = first.nextIndex()
        buffer.seek(first.indexToPosition())
        buffer.writeByte(newHeader.toInt())

        // Decrement buffer size
        sz--

        return element
    }

    /** Retrieves and removes [n] or all remaining elements of the buffer. */
    fun removeMany(n: Int): List<String> {
        val elements = mutableListOf<String>()
        val k = n.coerceAtMost(sz)
        repeat(k) {
            elements.add(remove())
        }

        return elements
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
            Log.w(TAG, "$errorPrefix Invalid header (expected '$HEAD', found '$head')")
            return false
        }
        val version = buf.readInt()
        if (VERSION != version) {
            Log.w(TAG, "$errorPrefix Version mismatch (expected $VERSION, found $version)")
            return false
        }

        globalIndex = buf.readLong()

        val elementSize = buf.readInt()
        if (ELEMENT_SIZE != elementSize) {
            Log.w(TAG, "$errorPrefix Wrong element size (expected $ELEMENT_SIZE, found $elementSize)")
            return false
        }
        val foundCapacity = buf.readInt()
        if (capacity != foundCapacity) {
            Log.w(TAG, "$errorPrefix Wrong capacity (expected $capacity, found $foundCapacity)")
            return false
        }

        // Validate file size
        val fileSize = bufferFile.length()
        if (this.fileSize != fileSize) {
            Log.w(TAG, "$errorPrefix Wrong file size (expected ${this.fileSize}, found $fileSize)")
            return false
        }

        // Validate first element
        var firstElementIndex = -1
        repeat(capacity) {
            buf.seek(it.indexToPosition())
            if (buf.readByte().isFirst()) {
                if (firstElementIndex != -1) {
                    return false
                }
                firstElementIndex = it
            }
        }
        if (firstElementIndex == -1) {
            return false
        }

        // Validate buffer consistency
        var cursor = firstElementIndex.indexToPosition()
        var len: Long = 0
        while (true) {
            buf.seek(cursor)
            cursor = cursor.incrementCursor()
            if (buf.readByte().isValid() && cursor.positionToIndex() != firstElementIndex) len++ else break
        }
        while (cursor.positionToIndex() != firstElementIndex) {
            buf.seek(cursor)
            cursor = cursor.incrementCursor()
            if (buf.readByte().isValid()) {
                return false
            }
        }

        return true
    }

    private fun openFile(bufferFile: File): RandomAccessFile {
        val buffer = RandomAccessFile(bufferFile, "rwd")

        // Locate first element
        first = 0
        buffer.seek(first.indexToPosition())
        while (first < capacity && !buffer.readByte().isFirst()) {
            buffer.seek((++first).indexToPosition())
        }

        // Locate last element
        last = first
        buffer.seek(last.indexToPosition())
        if (buffer.readByte().isValid()) {
            do {
                sz++  // count elements number
                last = last.nextIndex()
                buffer.seek(last.indexToPosition())
            } while (buffer.readByte().isValid() && last != first)
        }

        return buffer
    }

    private fun newFile(bufferFile: File): RandomAccessFile {
        if (bufferFile.exists()) {
            bufferFile.delete()
        }
        val buffer = RandomAccessFile(bufferFile, "rwd")

        // Allocate space
        buffer.seek(fileSize - 1)
        buffer.writeByte(0)
        buffer.seek(0)

        // Write header
        buffer.writeBytes(HEAD)
        buffer.writeInt(VERSION)
        buffer.writeLong(globalIndex)
        buffer.writeInt(ELEMENT_SIZE)
        buffer.writeInt(capacity)

        // Write first element header
        buffer.seek(FILE_HEADER_SIZE.toLong())
        buffer.writeByte(FIRST_FLAG.toInt())

        first = 0
        last = 0
        buffer.seek(first.indexToPosition())

        return buffer
    }

    private fun Byte.isFirst() = this and FIRST_FLAG == FIRST_FLAG

    private fun Byte.isValid() = this and VALID_FLAG == VALID_FLAG

    private fun Int.nextIndex(): Int = (this + 1) % capacity

    private fun Long.incrementCursor(): Long = this.positionToIndex().nextIndex().indexToPosition()

    private fun Long.positionToIndex(): Int = ((this - FILE_HEADER_SIZE) / ELEMENT_SIZE).toInt()

    private fun Int.indexToPosition(): Long = (FILE_HEADER_SIZE + (this * ELEMENT_SIZE)).toLong()
}