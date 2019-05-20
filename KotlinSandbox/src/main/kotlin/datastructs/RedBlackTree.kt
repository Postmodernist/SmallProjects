package datastructs

import java.util.*

/**
 * Red-black tree is a BST with additional properties:
 *
 * 1) Each node is either red or black.
 * 2) The root is black.
 * 3) All leaves (NIL) are black.
 * 4) If a node is red, then both its children are black.
 * 5) Every path from a given node to any of its descendant NIL nodes contains the same number of black nodes.
 */
class RedBlackTree<T : Comparable<T>> {

    var size: Int = 0
        private set

    private var root: Node<T>? = null

    fun insert(key: T) {
        insert(Node(key))
    }

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
        var x: Node<T> = z // x is red
        var y: Node<T>?
        // If x is a root, then the only violated property is 2, and we just make the root black.
        // If x is not a root, then it has a parent.
        while (x != root && x.parent!!.color == RED) {
            // The only property violated at this point is 4, because both x and its parent are red.
            val p: Node<T> = x.parent!!
            if (p === p.parent?.left) {
                // Since x.parent is red, it's not a root (root is black), and thus it has a parent.
                val pp: Node<T> = p.parent!! // pp starts black
                y = pp.right
                if (y?.color == RED) {
                    // Case 1: no matter if x is right or left child we do the same transformation
                    p.color = BLACK
                    y.color = BLACK
                    pp.color = RED
                    x = pp // x is red again
                } else {
                    if (x === p.right) {
                        // Case 2: rotate left immediately to transform the situation to Case 3
                        x = p // x is red yet again
                        rotateLeft(x)
                    }
                    // Case 3: some color changes and right rotation
                    p.color = BLACK
                    pp.color = RED
                    rotateRight(pp)
                    // The while loop terminates because property 4 is satisfied
                }
            } else {
                // This branch is symmetrical to the first branch.
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

    override fun toString(): String {
        return root?.showTree() ?: "null"
    }

    private data class Node<V : Comparable<V>>(val key: V) {
        var parent: Node<V>? = null
        var left: Node<V>? = null
        var right: Node<V>? = null
        var color: Boolean = BLACK

        fun showTree(): String {
            val sb = StringBuilder()
            val stack = ArrayDeque<Pair<Int, Node<V>>>()
            stack.push(Pair(0, this))
            while (!stack.isEmpty()) {
                val (t, x) = stack.pop()
                repeat(t) { sb.append("  ") }
                sb.append("${x.key}-${if (x.color) "B" else "R"}\n")
                if (x.left != null) stack.push(Pair(t + 1, x.left!!))
                if (x.right != null) stack.push(Pair(t + 1, x.right!!))
            }
            return sb.toString()
        }
    }

    private companion object {
        const val RED = false
        const val BLACK = true
    }
}
