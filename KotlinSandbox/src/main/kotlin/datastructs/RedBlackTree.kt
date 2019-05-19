package datastructs

class RedBlackTree<T : Comparable<T>> {

    var size: Int = 0
        private set

    private var root: Node<T>? = null

    /** Assumes x.right is not null. */
    private fun rotateLeft(x: Node<T>) {
        val y = x.right!!
        x.right = y.left
        if (y.left != null) y.left!!.parent = x
        val p = x.parent
        y.parent = p
        when {
            p == null -> root = y
            x === p.left -> p.left = y
            else -> p.right = y
        }
        y.left = x
        x.parent = y
    }

    /** Assumes x.left is not null. */
    private fun rotateRight(x: Node<T>) {
        val y = x.left!!
        x.left = y.right
        if (y.right != null) y.right!!.parent = x
        val p = x.parent
        y.parent = p
        when {
            p == null -> root = y
            x === p.left -> p.left = y
            else -> p.right = y
        }
        y.right = x
        x.parent = y
    }

    /** Assumes z.key is set. */
    private fun insert(z: Node<T>) {
        var y: Node<T>? = null
        var x: Node<T>? = root
        while (x != null) {
            y = x
            x = if (z.key < x.key) x.left else x.right
        }
        z.parent = y
        when {
            y == null -> root = z
            z.key < y.key -> y.left = z
            else -> y.right = z
        }
        z.left = null
        z.right = null
        z.color = RED
        fixAfterInsertion(z)
        size++
    }

    private fun fixAfterInsertion(z: Node<T>) {
        var x: Node<T> = z
        var y: Node<T>?
        while (x != root && x.parent!!.color == RED) {
            val p: Node<T> = x.parent!!
            if (p === p.parent?.left) {
                val pp: Node<T> = p.parent!!
                y = pp.right
                if (y?.color == RED) {
                    p.color = BLACK
                    y.color = BLACK
                    pp.color = RED
                    x = pp
                } else {
                    if (x === p.right) {
                        x = p
                        rotateLeft(x)
                    }
                    p.color = BLACK
                    pp.color = RED
                    rotateRight(pp)
                }
            } else if (p === p.parent?.right) {
                val pp: Node<T> = p.parent!!
                y = pp.left
                if (y?.color == RED) {
                    p.color = BLACK
                    y.color = BLACK
                    pp.color = RED
                    x = pp
                } else {
                    if (x === p.left) {
                        x = p
                        rotateRight(x)
                    }
                    p.color = BLACK
                    pp.color = RED
                    rotateLeft(pp)
                }
            }
        }
        root!!.color = BLACK
    }

    private data class Node<V : Comparable<V>>(val key: V) {
        var parent: Node<V>? = null
        var left: Node<V>? = null
        var right: Node<V>? = null
        var color: Boolean = BLACK
    }

    private companion object {
        const val RED = false
        const val BLACK = true
    }
}
