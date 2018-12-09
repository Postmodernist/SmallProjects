import scala.annotation.tailrec

object HigherOrderFunctions {
  def sumInts(a: Int, b: Int): Int = sum(x => x, a, b)

  def sum(f: Int => Int, a: Int, b: Int): Int = {
    @tailrec
    def iter(acc: Int, x: Int): Int = if (x > b) acc else iter(acc + f(x), x + 1)

    iter(0, a)
  }

  def sumCubes(a: Int, b: Int): Int = sum(x => x * x * x, a, b)

  def sumFactorials(a: Int, b: Int): Int = sum(Mathematical.factorial, a, b)
}
