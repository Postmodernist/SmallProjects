import kotlin.random.Random

fun number(cutoff: Double): Double {
    val first = Random.nextDouble()
    return if (first > cutoff) first else Random.nextDouble()
}

fun pWin(a: Double, b: Double, trials: Int = 30000): Double {
    val aWins = (0..trials).map { number(a) > number(b) }.sumBy { if (it) 1 else 0 }
    return aWins.toDouble() / trials
}

fun main() {
    println(pWin(0.6, 0.3))
}
