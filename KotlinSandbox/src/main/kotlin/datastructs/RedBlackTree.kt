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

    fun remove(key: T) {
        var x: Node<T>? = root
        while (x != null && x.key != key) {
            x = if (key < x.key) x.left else x.right
        }
        if (x != null) remove(x)
    }

    private fun rotateLeft(x: Node<T>?) {
        if (x?.right == null) return
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

    private fun rotateRight(x: Node<T>?) {
        if (x?.left == null) return
        val y = x.left!!
        x.left = y.right
        if (y.right != null) y.right!!.parent = x
        val p = x.parent
        y.parent = p
        when {
            p == null -> root = y
            x === p.right -> p.right = y
            else -> p.left = y
        }
        y.right = x
        x.parent = y
    }

    private fun insert(z: Node<T>) {
        var x: Node<T>? = root
        var y: Node<T>? = null
        while (x != null) {
            if (x == z) return // do not let duplicate keys
            y = x
            x = if (z.key < x.key) x.left else x.right
        }
        size++ // we're positive about inserting at this point
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
    }

    private fun fixAfterInsertion(node: Node<T>) {
        var x: Node<T> = node // x is red
        // If x is a root, then the only violated property is 2, and we just make the root black.
        // If x is not a root, then it has a parent.
        while (x !== root && colorOf(x.parent) == RED) {
            // The only property violated at this point is 4, because both x and its parent are red.
            var p: Node<T> = x.parent!! // (x !== root) -> (x.parent != null)
            if (p === p.parent?.left) {
                // (x.parent.color == RED) -> (x.parent !== root) -> (x.parent.parent != null)
                val pp: Node<T> = p.parent!! // pp starts black
                val y = pp.right
                if (colorOf(y) == RED) {
                    // Case 1: no matter if x is right or left child we do the same transformation
                    p.color = BLACK
                    y?.color = BLACK
                    pp.color = RED
                    x = pp // x is red again
                } else {
                    if (x === p.right) {
                        // Case 2: rotate left immediately to transform the situation to Case 3
                        x = p // x is red yet again
                        rotateLeft(x)
                        p = x.parent!! // x is guaranteed to have parent after rotation
                    }
                    // Case 3: some color changes and right rotation
                    p.color = BLACK
                    pp.color = RED
                    rotateRight(pp)
                    // The while loop terminates because property 4 is satisfied
                }
            } else { // symmetrical
                val pp: Node<T> = p.parent!!
                val y = pp.left
                if (colorOf(y) == RED) {
                    p.color = BLACK
                    y?.color = BLACK
                    pp.color = RED
                    x = pp
                } else {
                    if (x === p.left) {
                        x = p
                        rotateRight(x)
                        p = x.parent!!
                    }
                    p.color = BLACK
                    pp.color = RED
                    rotateLeft(pp)
                }
            }
        }
        root!!.color = BLACK // the tree is not empty at this point
    }

    private fun remove(z: Node<T>) {
        size--
        if (size == 0) { // removed the last node
            root = null
            return
        }
        if (z.left != null && z.right != null) { // z has two children
            val y = z.successor()!! // (z.right != null) -> (z.successor() != null)
            z.key = y.key // instead of moving y just copy the key
            // Now we only have to remove y, which may have a right child
            val x = y.right // child node that will take place of y
            if (x != null) { // there's a child
                transplant(y, x) // remove y and put x in its place
                if (y.color == BLACK) // we violated RB invariant
                    fixAfterDeletion(x)
            } else { // y has no children
                if (y.color == BLACK)
                    fixAfterDeletion(y) // use y as sentinel then detach it
                y.detach()
            }
        } else { // z has one or no children
            val x = if (z.left == null) z.right else z.left
            if (x != null) { // z has one child
                transplant(z, x)
                if (z.color == BLACK)
                    fixAfterDeletion(x)
            } else { // z has no children
                if (z.color == BLACK)
                    fixAfterDeletion(z) // use z as sentinel then detach it
                z.detach()
            }
        }
    }

    private fun transplant(x: Node<T>, y: Node<T>?) {
        // Attach y to x.parent
        val p = x.parent
        when {
            p == null -> root = y
            x === p.left -> p.left = y
            else -> p.right = y
        }
        y?.parent = p
        // Free x links.
        x.parent = null
        x.left = null
        x.right = null
    }

    private fun fixAfterDeletion(node: Node<T>?) {
        var x: Node<T> = node ?: return
        while (x !== root && x.color == BLACK) {
            val p = x.parent!! // (x !== root) -> (x.parent != null)
            if (x === p.left) {
                var y = p.right // sibling
                if (colorOf(y) == RED) {
                    y?.color = BLACK
                    p.color = RED
                    rotateLeft(p) // (p === x.parent) holds
                    y = p.right
                }
                if (colorOf(y?.left) == BLACK && colorOf(y?.right) == BLACK) {
                    y?.color = RED
                    x = p
                } else {
                    if (colorOf(y?.right) == BLACK) {
                        y?.left?.color = BLACK
                        y?.color = RED
                        rotateRight(y) // p is not affected
                        y = p.right
                    }
                    y?.color = p.color
                    p.color = BLACK
                    y?.right?.color = BLACK
                    rotateLeft(p)
                    x = root!! // (x !== root) -> (root != null)
                }
            } else { // symmetric
                var y = p.left
                if (colorOf(y) == RED) {
                    y?.color = BLACK
                    p.color = RED
                    rotateRight(p)
                    y = p.left
                }
                if (colorOf(y?.right) == BLACK && colorOf(y?.left) == BLACK) {
                    y?.color = RED
                    x = p
                } else {
                    if (colorOf(y?.left) == BLACK) {
                        y?.right?.color = BLACK
                        y?.color = RED
                        rotateLeft(y)
                        y = p.left
                    }
                    y?.color = p.color
                    p.color = BLACK
                    y?.left?.color = BLACK
                    rotateRight(p)
                    x = root!!
                }
            }
        }
        x.color = BLACK
    }

    override fun toString(): String {
        return root?.showTree() ?: "null"
    }

    fun toList(): List<T> {
        val a = ArrayList<T>()
        val stack = ArrayDeque<Node<T>>()
        val explored = HashSet<Node<T>>()
        stack.push(root)
        while (!stack.isEmpty()) {
            val x = stack.pop()
            if (x in explored) {
                a.add(x.key)
            } else {
                explored.add(x)
                if (x.right != null) stack.push(x.right)
                stack.push(x)
                if (x.left != null) stack.push(x.left)
            }
        }
        return a
    }

    private data class Node<V : Comparable<V>>(var key: V) {
        var parent: Node<V>? = null
        var left: Node<V>? = null
        var right: Node<V>? = null
        var color: Boolean = BLACK

        fun successor(): Node<V>? {
            var x = right ?: return null
            while (x.left != null) x = x.left!!
            return x
        }

        fun detach() {
            if (parent == null) return
            if (this === parent!!.left) {
                parent!!.left = null
            } else {
                parent!!.right = null
            }
            parent = null
        }

        fun showTree(): String {
            val sb = StringBuilder()
            val stack = ArrayDeque<Pair<Int, Node<V>>>()
            val explored = HashSet<Node<V>>()
            stack.push(Pair(0, this))
            while (!stack.isEmpty()) {
                val (t, x) = stack.pop()
                if (x in explored) {
                    repeat(t) { sb.append("  ") }
                    sb.append("${x.key}-${if (x.color) "B" else "R"}\n")
                } else {
                    if (x.left != null) stack.push(Pair(t + 1, x.left!!))
                    stack.push(Pair(t, x))
                    if (x.right != null) stack.push(Pair(t + 1, x.right!!))
                    explored.add(x)
                }
            }
            return sb.toString()
        }
    }

    private companion object {
        const val RED = false
        const val BLACK = true

        fun colorOf(x: Node<*>?): Boolean = x?.color ?: BLACK
    }
}
