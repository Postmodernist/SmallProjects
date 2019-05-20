package datastructs

import java.util.*

fun main() {
    val a = TreeMap<Int, String>()
    a.put(41, "")
    a.put(38, "")
    a.put(31, "")
    a.put(12, "")
    a.put(19, "")
    a.put(8, "")
    println(a)
    val b = RedBlackTree<Int>()
    b.insert(41)
    b.insert(38)
    b.insert(31)
    b.insert(12)
    b.insert(19)
    b.insert(8)
    println(b)
}