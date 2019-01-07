package algorithms

import scala.collection.mutable

object LongestIncSeq extends App {
  def myLis(xs: List[Int], x: Int = Int.MinValue): List[Int] = xs.filter(_ > x) match {
    case Nil => Nil
    case h :: t => List(h :: myLis(t, h), myLis(t)).maxBy(_.size)
  }

  def myLisMemo(xs: List[Int], x: Int = Int.MinValue)(implicit memo: mutable.Map[List[Int], List[Int]] = mutable.Map()): List[Int] = {
    xs.filter(_ > x) match {
      case Nil => Nil
      case ys if memo contains ys => memo(ys)
      case h :: t =>
        memo(h :: t) = List(h :: myLisMemo(t, h), myLisMemo(t)).maxBy(_.size)
        memo(h :: t)
    }
  }

  /** Longest increasing sequence (recursive) */
  def lisR(a: Array[Int], i: Int)(implicit t: mutable.Map[Int, Int] = mutable.Map()): Int = {
    if (!t.contains(i)) {
      t(i) = 1
      for (j <- 0 until i; if a(j) < a(i)) {
        t(i) = math.max(t(i), lisR(a, j) + 1)
      }
    }
    t(i)
  }

  /** Longest increasing sequence (iterative) */
  def lis(a: Array[Int]): List[Int] = {
    val t = new Array[Int](a.length)
    val prev = new Array[Int](a.length)
    t.indices.foreach {
      t(_) = 1
      prev(_) = -1
    }
    for (i <- a.indices; j <- 0 until i; if a(j) < a(i) && t(i) < t(j) + 1) {
      t(i) = t(j) + 1
      prev(i) = j
    }

    // Reconstruct
    var rec = List[Int]()
    var current = t.indexOf(t.max)
    while (current >= 0) {
      rec = current :: rec
      current = prev(current)
    }
    rec.map(a(_))
  }

  println(myLis(List(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)))
  println(myLisMemo(List(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)))

  val a = Array(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)
  println(a.indices.map(lisR(a, _)).max)

  println(lis(Array(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)))
}
