package functional

class Opt<T>(val v: T?, val isValid: Boolean)

infix fun <A, B, C> ((B) -> Opt<C>).fish(f: (A) -> Opt<B>): (A) -> Opt<C> = { x ->
    val y = f(x)
    if (!y.isValid) Opt(null, false) else this(y.v!!)
}

fun main() {
    val f: (Int) -> Opt<Int> = { x ->
        if (x >= 0) Opt(x % 10, true) else Opt(null, false)
    }

    val g: (Int) -> Opt<String> = { x ->
        if (x % 2 == 0) Opt("even", true) else Opt(null, false)
    }

    val log: (Int) -> Unit = { x ->
        with((g fish f)(x)) { if (isValid) println(v) else println("oops...") }
    }

    log(-42)
    log(42)
    log(111)
}
