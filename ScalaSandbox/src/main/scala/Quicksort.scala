object Quicksort extends App {

  def qsort[T](l: List[T])(implicit ord: Ordering[T]): List[T] = l match {
    case Nil => Nil
    case x :: xs =>
      val as = xs.filter(ord.lt(_, x))
      val bs = x :: xs.filter(ord.eq(_, x))
      val cs = xs.filter(ord.gt(_, x))
      qsort(as) ::: bs ::: qsort(cs)
  }

  println(qsort(List(4, 3, 2, 1)))
  println(qsort(List('b', 'z', 'd', 'x', 'k')))
  println(qsort(List("foo", "bar", "baz", "qux", "quux", "quuz", "corge", "grault", "garply", "waldo")))
}
