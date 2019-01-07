package algorithms

import scala.collection.mutable

object EditDistance extends App {

  /** Editing distance (recursive) */
  def editDistanceR(a: String, b: String, i: Int, j: Int)(implicit t: mutable.Map[(Int, Int), Int] = mutable.Map()): Int = {
    if (!t.contains((i, j))) {
      if (i == 0) {
        t((i, j)) = j
      } else if (j == 0) {
        t((i, j)) = i
      } else {
        val diff = if (a(i - 1) == b(j - 1)) 0 else 1
        t((i, j)) = List(
          editDistanceR(a, b, i - 1, j) + 1,
          editDistanceR(a, b, i, j - 1) + 1,
          editDistanceR(a, b, i - 1, j - 1) + diff
        ).min
      }
    }
    t((i, j))
  }

  /** Editing distance (iterative) */
  def editDistance(a: String, b: String): Int = {
    val t = Array.ofDim[Int](a.length + 1, b.length + 1)
    for (i <- t.indices; j <- t(0).indices) t(i)(j) = Int.MaxValue
    for (i <- t.indices) t(i)(0) = i
    for (j <- t(0).indices) t(0)(j) = j

    for (i <- 1 until t.length; j <- 1 until t(0).length) {
      val diff = if (a(i - 1) == b(j - 1)) 0 else 1
      t(i)(j) = List(
        t(i - 1)(j) + 1,
        t(i)(j - 1) + 1,
        t(i - 1)(j - 1) + diff
      ).min
    }

    t(a.length)(b.length)
  }

  println(editDistanceR(a = "editing", b = "distance", i = 7, j = 8))
  println(editDistance(a = "editing", b = "distance"))
}
