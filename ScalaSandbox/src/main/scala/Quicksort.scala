object Quicksort extends App {

  def qsort[T](l: List[T])(implicit ord: Ordering[T]): List[T] = l match {
    case Nil => Nil
    case x :: xs =>
      val ys = xs.filter(ord.lteq(_, x))
      val zs = xs.filter(ord.gt(_, x))
      qsort(ys) ::: List(x) ::: qsort(zs)
  }

  println(qsort(List(4, 3, 2, 1)))
  println(qsort(List('b', 'z', 'd', 'x', 'k')))
  println(qsort(List("foo", "bar", "baz", "qux", "quux", "quuz", "corge", "grault", "garply", "waldo")))
}
