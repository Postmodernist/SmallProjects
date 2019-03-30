package various

fun <T> loop(init: T, cond: (T) -> Boolean, next: (T) -> T, block: (T) -> Unit) {
    var i = init
    while (cond(i)) {
        block(i)
        i = next(i)
    }
}

fun main() {
    val a = arrayListOf<() -> Unit>()
    loop(0, { it < 10 }, { it + 1 }) {
        a += { print(it) }
    }
    a.forEach { it() }
}