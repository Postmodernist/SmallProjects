package tutorial

object Streams extends App {
  val xs = Stream.cons(1, Stream.cons(2, Stream.empty))

  def streamRange(lo: Int, hi: Int): Stream[Int] =
    if (lo >= hi) Stream.empty
    else lo #:: streamRange(lo + 1, hi)

  streamRange(0, 10).take(3).foreach(x => print(s"$x "))
  println()

  val str: Stream[Int] = 1 #:: str.map(_ + 1)
  str.take(10).foreach(x => print(s"$x "))
  println()

  def fibs(a: Int = 1, b: Int = 1): Stream[Int] = a #:: fibs(b, a + b)

  fibs().take(10).foreach(x => print(s"$x "))
}
