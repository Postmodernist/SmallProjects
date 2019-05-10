package datastructs

import java.util.*

class BinaryHeap<T : Comparable<T>> private constructor() : Heap<T> {

    private lateinit var heap: Array<Any?>

    /** The number of elements in the heap. */
    var size: Int = 0
        private set

    constructor(initialCapacity: Int = DEFAULT_CAPACITY) : this() {
        heap = Array(initialCapacity) { null }
    }

    constructor(c: Collection<T>) : this() {
        heap = c.toTypedArray()
        heapify()
        size = c.size
    }

    @Suppress("UNCHECKED_CAST")
    override fun peek(): T? = if (size == 0) null else heap[0] as T

    /** Removes the root element from this heap. */
    @Suppress("UNCHECKED_CAST")
    override fun extract(): T? {
        if (size == 0) return null
        val s = --size
        val root = heap[0] as T
        val x = heap[s] as T
        heap[s] = null
        if (s != 0) siftDown(0, x)
        return root
    }

    /** Inserts the specified key into this heap. */
    override fun insert(key: T): Boolean {
        val s = size
        if (s >= heap.size) grow(s + 1)
        size = s + 1
        if (s == 0) {
            heap[0] = key
        } else {
            siftUp(s, key)
        }
        return true
    }

    /**
     * Removes a single instance of the specified key from this heap,
     * if it is present.
     */
    @Suppress("UNCHECKED_CAST")
    override fun remove(key: T): Boolean {
        val i = heap.indexOf(key)
        if (i == -1) return false
        val s = --size
        if (s == i) {
            heap[i] = null
        } else {
            val moved = heap[s] as T
            heap[s] = null
            siftDown(i, moved)
            if (heap[i] == moved) siftUp(i, moved)
        }
        return true
    }

    /**
     * Swaps an instance of the specified key with newKey.
     */
    override fun modify(key: T, newKey: T): Boolean {
        val i = heap.indexOf(key)
        if (i == -1) return false
        if (i >= size ushr 1) { // leaf element
            siftUp(i, newKey)
        } else { // non-leaf element
            siftDown(i, newKey)
            if (heap[i] == newKey) siftUp(i, newKey)
        }
        return true
    }

    private fun Int.parent() = (this - 1) ushr 1

    private fun Int.leftChild() = (this shl 1) + 1

    /**
     * Increases the capacity of the array.
     *
     * @param minCapacity the desired minimum capacity
     */
    private fun grow(minCapacity: Int) {
        if (minCapacity < 0 || minCapacity > Int.MAX_VALUE - 8) {
            throw RuntimeException("Heap size overflow")
        }
        val oldCap = heap.size
        var newCap = oldCap + if (oldCap < 64) oldCap + 2 else oldCap ushr 1
        if (newCap < 0 || newCap > Int.MAX_VALUE - 8) {
            newCap = minCapacity
        }
        heap = heap.copyOf(newCap)
    }

    /**
     * Inserts item x at position i, maintaining heap invariant by
     * promoting x up the tree until it is greater than or equal to
     * its parent, or is the root.
     */
    @Suppress("UNCHECKED_CAST")
    private fun siftUp(i: Int, x: T) {
        var k = i
        while (k > 0) {
            val parent = k.parent()
            val p = heap[parent]
            if (x >= (p as T)) break
            heap[k] = p
            k = parent
        }
        heap[k] = x
    }

    /**
     * Inserts item x at position i, maintaining heap invariant by
     * demoting x down the tree repeatedly until it is less than or
     * equal to its children or is a leaf.
     */
    @Suppress("UNCHECKED_CAST")
    private fun siftDown(i: Int, x: T) {
        var k = i
        val half = size ushr 1  // [half - 1] is the last non-leaf element
        while (k < half) {
            var child = k.leftChild()
            var c = heap[child]
            val right = child + 1
            if (right < size && (c as T) > (heap[right] as T)) {
                child = right
                c = heap[right]
            }
            if (x <= (c as T)) break
            heap[k] = c
            k = child
        }
        heap[k] = x
    }

    /**
     * Establishes the heap invariant in the entire tree, assuming
     * nothing about the order of the elements prior to the call.
     */
    @Suppress("UNCHECKED_CAST")
    private fun heapify() {
        var i = (size ushr 1) - 1
        while (i >= 0) {
            siftDown(i, heap[i] as T)
            i--
        }
    }

    companion object {
        private const val DEFAULT_CAPACITY = 10

        fun toArray(h: BinaryHeap<Int>): Array<Int> = Arrays.copyOf(h.heap, h.size, Array<Int>::class.java)
    }
}
