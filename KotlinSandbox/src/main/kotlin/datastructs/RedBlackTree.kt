package datastructs

class RedBlackTree<T : Comparable<T>> {

    private data class Node<V : Comparable<V>>(val key: V) {
        var parent: Node<V>? = null
        var prev: Node<V>? = null
        var next: Node<V>? = null
        var color: Boolean = BLACK
    }

    private companion object {
        const val RED = false
        const val BLACK = true
    }
}
