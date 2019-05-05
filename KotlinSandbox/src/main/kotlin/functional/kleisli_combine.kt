package functional

import kotlin.math.sqrt

/* Optional data type */
class Opt<T>(val value: T? = null, val isValid: Boolean = false) {
    constructor(value: T) : this(value, true)
}

/* Composition */
infix fun <A, B, C> ((B) -> Opt<C>).after(f: (A) -> Opt<B>): (A) -> Opt<C> = { x ->
    val g = this
    val y = f(x)
    if (y.isValid) g(y.value!!) else Opt()
}

/* Identity */
fun <T> optId(x: T?): Opt<T> = if (x != null) Opt(x) else Opt()

val safeSqrt: (Double) -> Opt<Double> = { x -> if (x >= 0) Opt(sqrt(x)) else Opt() }

val safeReciprocal: (Double) -> Opt<Double> = { x -> if (x != 0.0) Opt(1 / x) else Opt() }

fun main() {
    val sqrtOfReciprocal = safeSqrt after safeReciprocal
    val log: (Double) -> Unit = { x ->
        val opt = sqrtOfReciprocal(x)
        if (opt.isValid) println(opt.value) else println("oops...")
    }

    log(42.0) // success
    log(-42.0) // fails on safeSqrt
    log(0.0) // fails on safeReciprocal
}
