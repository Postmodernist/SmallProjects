package datastructs

class FibonacciHeap<T : Comparable<T>> : Heap<T> {

    var min: Node<T>? = null
        private set

    override fun peek(): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun extract(): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(element: T): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(element: T): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun modify(element: T, newElement: T): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class Node<E : Comparable<E>>(var element: E) {
        var parent: Node<E>? = null
        var child: Node<E>? = null
        var prev: Node<E>? = null
        var next: Node<E>? = null
        var rank: Int = 0
        var mark: Boolean = false
    }
}