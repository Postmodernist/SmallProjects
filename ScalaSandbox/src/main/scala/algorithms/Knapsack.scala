package algorithms

import scala.collection.mutable

object Knapsack extends App {

  /** Knapsack with repetition (recursive). */
  def knapsackRepR(ws: Array[Int], vs: Array[Int], u: Int)(implicit t: mutable.Map[Int, Int] = mutable.Map()): Int = {
    if (!t.contains(u)) {
      t += (u -> 0)
      for (i <- ws.indices; if ws(i) <= u) {
        t(u) = math.max(t(u), knapsackRepR(ws, vs, u - ws(i)) + vs(i))
      }
    }
    t(u)
  }

  /** Knapsack with repetition (iterative). */
  def knapsackRep(W: Int, ws: Array[Int], vs: Array[Int]): Int = {
    val t = new Array[Int](W + 1)
    for (u <- 1 to W; i <- ws.indices; if ws(i) <= u) {
      t(u) = math.max(t(u), t(u - ws(i)) + vs(i))
    }
    t(W)
  }

  /** Knapsack w/o repetition (recursive). */
  def knapsackR(ws: Array[Int], vs: Array[Int], u: Int, i: Int)(implicit t: mutable.Map[(Int, Int), Int] = mutable.Map()): Int = {
    if (!t.contains((u, i))) {
      if (i == 0) {
        t((u, i)) = 0
      } else {
        t((u, i)) = knapsackR(ws, vs, u, i - 1)
        if (u >= ws(i - 1)) {
          t((u, i)) = math.max(t((u, i)), knapsackR(ws, vs, u - ws(i - 1), i - 1) + vs(i - 1))
        }
      }
    }
    t((u, i))
  }

  /** Knapsack w/o repetition (iterative). */
  def knapsack(W: Int, ws: Array[Int], vs: Array[Int]): Int = {
    val t = Array.ofDim[Int](W + 1, ws.length + 1)
    for (u <- 0 to W; i <- 1 to ws.length) {
      t(u)(i) = t(u)(i - 1)
      if (ws(i - 1) <= u) {
        t(u)(i) = math.max(t(u)(i), t(u - ws(i - 1))(i - 1) + vs(i - 1))
      }
    }
    t.last.last
  }

  /** Knapsack w/o repetition (brute force) */
  def knapsackBf(W: Int, ws: Array[Int], vs: Array[Int], items: List[Int] = List(), last: Int = -1): Int =
    if (last == ws.length - 1) {
      (for (i <- items) yield vs(i)).sum
    } else {
      var value = knapsackBf(W, ws, vs, items, last + 1)
      val weight = items.map(ws(_)).sum
      if (weight + ws(last + 1) <= W) {
        value = math.max(value, knapsackBf(W, ws, vs, items :+ (last + 1), last + 1))
      }
      value
    }

  println(knapsackRepR(ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9), u = 10))
  println(knapsackRep(W = 10, ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9)))
  println(knapsackR(ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9), u = 10, i = 4))
  println(knapsack(W = 10, ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9)))
  println(knapsackBf(W = 10, ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9)))
}
