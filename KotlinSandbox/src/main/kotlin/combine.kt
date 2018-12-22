infix fun <T1, T2, T3> ((T2) -> T3).combine(f: (T1) -> T2) = { x: T1 -> this(f(x)) }
infix fun <T1, T2, T3> ((T1) -> T2).andThen(f: (T2) -> T3) = { x: T1 -> f(this(x)) }

val double = { x: Int -> x * 2 }

val quad = double combine double

fun main(args: Array<String>) {
    (quad andThen { println(it) })(3)
}
