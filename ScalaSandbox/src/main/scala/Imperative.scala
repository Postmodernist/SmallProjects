import scala.annotation.tailrec

object Imperative extends App {

  @tailrec
  def iterate(n: Int, f: Int => Int, acc: Int): Int =
    if (n == 0) acc else iterate(n - 1, f, f(acc))

  def square(x: Int) = x * x

  println(iterate(1, square, 3))

  for (i <- 1 until 5; j <- (0 until 3).reverse) {
    print(s"($i $j) ")
  }
  println

  (1 to 5) foreach { i => print(i) }
}
