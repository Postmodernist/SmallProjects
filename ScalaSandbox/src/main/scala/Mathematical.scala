import scala.annotation.tailrec

trait Y {
  def f(x: Y, n: Int): Int
}

object Mathematical extends App {
  def cube(x: Int): Int = {
    x * x * x
  }

  //    println(cube(3))

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

  //    println(sqrt(2))

  def factorial(n: Int): Int = if (n == 0) 1 else factorial(n - 1) * n

  //    println(factorial(10))

  def fact(n: Int) = ((x: Y) => x.f(x, n)) ((x, n) => if (n == 0) 1 else n * x.f(x, n - 1))

  //    println(fact(10))

  @tailrec
  def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

  println(gcd(14, 21))
}
