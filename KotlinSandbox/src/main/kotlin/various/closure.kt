package various

typealias Block = () -> Unit

fun test1() {
    val fis = mutableListOf<Block>()
    for (i in 0..4) {
        fis.add { print(i) }
    }
    for (fi in fis) {
        fi()
    }
    println()
}

fun test2() {
    val fis = mutableListOf<Block>()
    var outer: Int
    for (i in 0..4) {
        outer = i
        fis.add { print(outer) }
    }
    for (fi in fis) {
        fi()
    }
    println()
}

fun test3() {
    val fis = mutableListOf<Block>()
    for (i in 0..4) {
        val inner: Int = i
        fis.add { print(inner) }
    }
    for (fi in fis) {
        fi()
    }
    println()
}

fun main() {
    test1()
    test2()
    test3()
}
