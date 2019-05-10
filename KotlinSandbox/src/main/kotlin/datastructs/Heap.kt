package datastructs

interface Heap<T : Comparable<T>> {
    fun peek(): T?
    fun extract(): T?
    fun insert(key: T): Boolean
    fun remove(key: T): Boolean
    fun modify(key: T, newKey: T): Boolean
}