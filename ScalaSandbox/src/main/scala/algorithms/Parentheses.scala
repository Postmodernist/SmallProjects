package algorithms

object Parentheses extends App {
  val add: (Int, Int) => Int = _ + _
  val sub: (Int, Int) => Int = _ - _
  val mul: (Int, Int) => Int = _ * _

  def minAndMax(i: Int, j: Int, m: Array[Array[Int]], M: Array[Array[Int]], ops: List[(Int, Int) => Int]): (Int, Int) = {
    var min = Int.MaxValue
    var max = Int.MinValue
    for (k <- i until j) {
      val a = ops(k)(M(i)(k), M(k + 1)(j))
      val b = ops(k)(M(i)(k), m(k + 1)(j))
      val c = ops(k)(m(i)(k), M(k + 1)(j))
      val d = ops(k)(m(i)(k), m(k + 1)(j))
      min = List(min, a, b, c, d).min
      max = List(max, a, b, c, d).max
    }
    (min, max)
  }

  def parentheses(ds: Array[Int], ops: List[(Int, Int) => Int]): Int = {
    val n = ds.length
    val m = Array.ofDim[Int](n, n)
    val M = Array.ofDim[Int](n, n)
    for (i <- ds.indices) {
      m(i)(i) = ds(i)
      M(i)(i) = ds(i)
    }

    for (s <- 1 until n; i <- 0 until n - s; j = i + s) {
      minAndMax(i, j, m, M, ops) match {
        case (min, max) =>
          m(i)(j) = min
          M(i)(j) = max
      }
    }

    M(0).last
  }

  val ds = Array(5, 8, 7, 4, 8, 9)
  val ops = List(sub, add, mul, sub, add)
  println(parentheses(ds, ops))
}
