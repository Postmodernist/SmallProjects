package various

fun pyth(n: Int) =
        (1..n).flatMap { x ->
            (x..n).flatMap { y ->
                (y..n).mapNotNull { z ->
                    if (x * x + y * y == z * z) Triple(x, y, z) else null
                }
            }
        }

fun main() {
    println(pyth(25))
}
