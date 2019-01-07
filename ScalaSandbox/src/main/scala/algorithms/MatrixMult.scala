package algorithms

import scala.collection.mutable

object MatrixMult extends App {

  /** Chain matrix multiplication (recursive) */
  def matrixMultR(m: Array[Int], i: Int, j: Int)(implicit t: mutable.Map[(Int, Int), Int] = mutable.Map()): Int = {
    if (!t.contains((i, j))) {
      if (j == i + 1) {
        t((i, j)) = 0
      } else {
        t((i, j)) = Int.MaxValue
        for (k <- i + 1 until j) {
          t((i, j)) = math.min(t((i, j)), matrixMultR(m, i, k) + matrixMultR(m, k, j) + m(i) * m(j) * m(k))
        }
      }
    }
    t((i, j))
  }

  /** Chain matrix multiplication (iterative) */
  def matrixMult(m: Array[Int]): Int = {
    val n = m.length - 1
    val t = Array.ofDim[Int](m.length, m.length)
    for (i <- t.indices; j <- t(0).indices) t(i)(j) = Int.MaxValue
    for (i <- 0 until n) t(i)(i + 1) = 0

    for (s <- 2 to n; i <- 0 to n - s; j = i + s; k <- i + 1 until j) {
      t(i)(j) = math.min(t(i)(j), t(i)(k) + t(k)(j) + m(i) * m(j) * m(k))
    }

    // Print out the table
    for (i <- t.indices) {
      for (j <- t(0).indices) print(if (t(i)(j) == Int.MaxValue) "    -1" else " %5d".format(t(i)(j)))
      println()
    }

    t(0)(n)
  }

  println(matrixMultR(m = Array(50, 20, 1, 10, 100), i = 0, j = 4))
  println(matrixMult(m = Array(50, 20, 1, 10, 100)))
}
