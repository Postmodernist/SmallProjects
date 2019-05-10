package datastructs

class FibonacciHeap<T : Comparable<T>> : Heap<T> {

    /** Root node of the heap. */
    private var root: Node<T>? = null

    /** The number of nodes in the heap. */
    var size: Int = 0
        private set

    override fun peek(): T? = root?.key

    override fun extract(): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Inserts the specified key into this heap. */
    override fun insert(key: T): Boolean {
        val node = Node(key)
        var r = root
        if (r == null) {
            r = node
        } else {
            r.splice(node)
            if (node.key < r.key) r = node
        }
        root = r
        size++
        return true
    }

    override fun remove(key: T): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun modify(key: T, newKey: T): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Merges other heap into this heap. Other heap becomes empty after the procedure completes. */
    fun merge(other: FibonacciHeap<T>) {
        var r = root
        val ro = other.root ?: return
        if (r == null) {
            r = ro
        } else {
            r.splice(ro)
            if (ro.key < r.key) r = ro
        }
        root = r
        size += other.size
        other.root = null
        other.size = 0
    }

    private data class Node<V : Comparable<V>>(var key: V) {
        var parent: Node<V>? = null    // parent node
        var child: Node<V>? = null     // any node in child list
        var prev: Node<V>? = this      // previous sibling
        var next: Node<V>? = this      // next sibling
        var rank: Int = 0              // the number of children
        var mark: Boolean = false      // whether node has lost a child since the last time it was parented

        /** Splice this list with other list. */
        fun splice(other: Node<V>) {
            prev!!.next = other
            other.prev!!.next = this
            val tmp = prev
            prev = other.prev
            other.prev = tmp
        }
    }
}