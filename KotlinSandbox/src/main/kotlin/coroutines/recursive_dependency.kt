package coroutines

fun seq(): Sequence<Int> = sequence {
    yieldAll(0..3)
    val x = { seq() }
    yieldAll(x())
}

fun main() {
    seq().take(10).forEach { println(it) }
}
