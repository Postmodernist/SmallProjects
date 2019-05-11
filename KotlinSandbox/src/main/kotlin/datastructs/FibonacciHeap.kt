package datastructs

import kotlin.math.log
import kotlin.math.sqrt

class FibonacciHeap<T : Comparable<T>> : Heap<T> {

    /** The number of nodes in the heap. */
    var size: Int = 0
        private set

    /** Root node of the heap. */
    private var root: Node<T>? = null

    /** Golden ratio. */
    private val phi: Double = (1 + sqrt(5.0)) / 2.0

    override fun peek(): T? = root?.key

    /** Removes the root element from this heap. */
    override fun extract(): T? {
        val x = root ?: return null
        if (x.child != null) {
            // Move children to the root list
            var c: Node<T> = x.child!!
            var i = 0
            while (i < x.rank) {
                c.parent = null
                c = c.next
                i++
            }
            x.splice(c)
        }
        x.softDetach()
        if (x === x.next) {
            root = null
        } else {
            root = x.next
            consolidate()
        }
        size--
        return x.key
    }

    private fun consolidate() {
        val n = log(size.toDouble(), phi).toInt() // node rank upper bound
        val ranks = Array<Node<T>?>(n) { null }
        var r = root!!
        var x = r.next
        while (x !== r) {
            val next = x.next
            combineNodesOfEqualRanks(x, ranks)
            x = next
        }
        r = combineNodesOfEqualRanks(x, ranks)
        // Find new root
        var min = r
        x = r.next
        while (x !== r) {
            if (x.key < min.key) min = x
            x = x.next
        }
        root = min
    }

    private fun combineNodesOfEqualRanks(node: Node<T>, ranks: Array<Node<T>?>): Node<T> {
        var x = node
        var i = x.rank
        while (ranks[i] != null) {
            var y = ranks[i]!! // another node with the same rank
            if (x.key > y.key) {
                val z = x
                x = y
                y = z
            }
            y.parentTo(x)
            ranks[i] = null
            i++
        }
        ranks[i] = x
        return x
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

    /**
     * Removes a single instance of the specified key from this heap,
     * if it is present.
     */
    override fun remove(key: T): Boolean {
        val x = root?.find(key) ?: return false
        decreaseKey(x, root!!.key)
        root = x
        extract()
        return true
    }

    /** Decreases key value of the node. */
    private fun decreaseKey(node: Node<T>, k: T) {
        if (k > node.key) throw RuntimeException("New key is greater than the current key")
        node.key = k
        val p = node.parent
        if (p != null && node.key < p.key) {
            cut(node)
            cascadingCut(p)
        }
        if (node.key < root!!.key) root = node
    }

    /** Detaches node from its parent and inserts it into root list. */
    private fun cut(node: Node<T>) {
        node.unparent()
        root!!.splice(node)
    }

    /** Cuts marked parent nodes until hits first unmarked. Marks that node. */
    private fun cascadingCut(node: Node<T>) {
        val p = node.parent ?: return
        if (node.mark) {
            cut(node)
            cascadingCut(p)
        } else {
            node.mark = true
        }
    }

    override fun modify(key: T, newKey: T): Boolean {
        TODO("not implemented")
    }

    /**
     * Merges other heap into this heap. Other heap becomes empty
     * after the procedure completes.
     */
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
        var parent: Node<V>? = null     // parent node
        var child: Node<V>? = null      // any node in child list
        var prev: Node<V> = this        // previous sibling
        var next: Node<V> = this        // next sibling
        var rank: Int = 0               // the number of children
        var mark: Boolean = false       // whether node has lost a child since the last time it was parented

        /** Splice this list with other list. */
        fun splice(other: Node<V>) {
            prev.next = other
            other.prev.next = this
            val tmp = prev
            prev = other.prev
            other.prev = tmp
        }

        /** Removes this node from the list leaving its structural attributes unchanged. */
        fun softDetach() {
            prev.next = next
            next.prev = prev
        }

        /** Removes this node from the list and makes it a singleton list. */
        fun detach() {
            parent = null
            prev.next = next
            next.prev = prev
            prev = this
            next = this
            mark = false
        }

        /**
         * Makes this node a child of other node. Assumes this node is
         * in the root list of the heap.
         */
        fun parentTo(other: Node<V>) {
            detach()
            if (other.child != null) {
                other.child!!.splice(this)
            } else {
                other.child = this
            }
            other.rank++
            parent = other
        }

        /**
         * Detaches this node from its parent, decrementing parent's rank.
         * Assumes parent exists.
         */
        fun unparent() {
            parent!!.rank--
            detach()
        }

        /** Finds a node with given key in this list and down the hierarchy recursively.*/
        fun find(key: V): Node<V>? {
            var x = this
            do {
                if (x.key == key) return x
                if (key > x.key && x.child != null) {
                    val c = x.child!!
                    val y = c.find(key)
                    if (y != null) return y
                }
                x = x.next
            } while (x !== this)
            return null
        }
    }
}