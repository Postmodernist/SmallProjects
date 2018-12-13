object Streams extends App {
  val xs = Stream.cons(1, Stream.cons(2, Stream.empty))

  def streamRange(lo: Int, hi: Int): Stream[Int] =
    if (lo >= hi) Stream.empty
    else lo #:: streamRange(lo + 1, hi)

  streamRange(0, 10).take(3).foreach(x => print(s"$x "))
}
