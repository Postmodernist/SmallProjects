package various

/**
 * Finds the index of the first occurrence of the specified value.
 * Returns list.size when all elements in the list do not exceed the specific value or when the list is empty.
 */
fun binSearch(a: IntArray, x: Int): Int {
    var l = 0
    var r = a.size
    while (l < r) {
        val m = l + (r - l) / 2
        if (a[m] < x) {
            l = m + 1
        } else {
            r = m
        }
    }
    return l
}

fun main() {
    check(binSearch(intArrayOf(), 2) == 0)
    val a = intArrayOf(1, 2, 7, 7, 7, 9, 15)
    check(binSearch(a, 0) == 0) { println(binSearch(a, 0)) }
    check(binSearch(a, 1) == 0) { println(binSearch(a, 1)) }
    check(binSearch(a, 2) == 1) { println(binSearch(a, 2)) }
    check(binSearch(a, 7) == 2) { println(binSearch(a, 7)) }
    check(binSearch(a, 10) == 6) { println(binSearch(a, 10)) }
    check(binSearch(a, 100) == 7) { println(binSearch(a, 100)) }
    val b = intArrayOf(6, 7, 7, 7, 9, 9)
    check(binSearch(b, 7) == 1) { println(binSearch(b, 7)) }
    println("Tests passed")
}
