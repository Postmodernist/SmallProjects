package algorithms

object LongestIncSeq extends App {
  def lis(xs: List[Int], x: Int = Int.MinValue): List[Int] = xs.filter(_ > x) match {
    case Nil => Nil
    case h :: t => List(h :: lis(t, h), lis(t)).maxBy(_.size)
  }

  def lisMemo(xs: List[Int], x: Int = Int.MinValue, memo: Map[List[Int], List[Int]] = Map()): List[Int] = {
    xs.filter(_ > x) match {
      case Nil => Nil
      case ys if memo contains ys => memo.apply(ys)
      case h :: t =>
        val res = List(h :: lisMemo(t, h, memo), lisMemo(t, memo = memo)).maxBy(_.size)
        memo + ((h :: t) -> res)
        res
    }
  }

  println(lisMemo(List()))
  println(lisMemo(List(1)))
  println(lisMemo(List(1, 2, 4)))
  println(lisMemo(List(1, 3, 2, 4)))
  println(lisMemo(List(1, 3, 2, 3)))
  println(lisMemo(List(5, 2, 4)))
  println(lisMemo(List(4, 2, 1)))
  println(lisMemo(List(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)))
}
