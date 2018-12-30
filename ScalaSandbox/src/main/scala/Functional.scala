object Functional extends App {
  val double: Int => Int = _ * 2
  val quad = double compose double
  (quad andThen println) (3)

  println(List(1, 2, 3).product)

  val xs = Stream from 1
  (xs take 10) foreach print
  println

  val average: List[Double] => Double = l => l.sum / l.size
  println(average(List(1, 2, 3)))

  val ys = List(0, 1, 2, 3, 4)
  println(ys dropRight 1)
  println(ys take (ys.length - 1))
}
