package functional

import kotlin.math.sqrt

/* Data type Optional */
class Opt<T>(val value: T? = null, val isValid: Boolean = false) {
    constructor(value: T) : this(value, true)
}

/* Composition */
infix fun <A, B, C> ((B) -> Opt<C>).after(f: (A) -> Opt<B>): (A) -> Opt<C> = { x ->
    val y = f(x)
    if (!y.isValid) Opt(null, false) else this(y.value!!)
}

/* Identity */
fun <T> optId(x: T?): Opt<T> = if (x != null) Opt(x) else Opt()

val safeSqrt: (Double) -> Opt<Double> = { x -> if (x >= 0) Opt(sqrt(x)) else Opt() }

val safeReciprocal: (Double) -> Opt<Double> = { x -> if (x != 0.0) Opt(1 / x) else Opt() }

fun main() {
    val sqrtReciprocal = safeSqrt after safeReciprocal
    val log: (Double) -> Unit = { x ->
        val opt = sqrtReciprocal(x)
        if (opt.isValid) println(opt.value) else println("oops...")
    }

    log(42.0)
    log(-42.0)
    log(0.0)
}
