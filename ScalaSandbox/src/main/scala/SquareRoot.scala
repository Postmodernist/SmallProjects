object SquareRoot extends App {

    def sqrt(x: Double) = {
        def sqrtIter(guess: Double): Double =
            if (isGoodEnough(guess)) guess
            else sqrtIter(improve(guess))

        def improve(guess: Double) =
            (guess + x / guess) / 2

        def isGoodEnough(guess: Double) =
            Math.abs(guess * guess - x) < 0.001

        sqrtIter(1.0)
    }

    println(sqrt(2))
}
