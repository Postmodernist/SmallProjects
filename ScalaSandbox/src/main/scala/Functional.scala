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

  val f: PartialFunction[Int, Int] = {
    case x: Int if x % 2 == 0 => x
  }
  val a = Stream.from(0).collect(f).take(5).toList
  println(a)
}


object Functional2 extends App {

  // Functor maps between categories preserving structure.
  // A --f-> B => F[A] --F[f]-> F[B]

  trait Functor[F[_]] {
    def fmap[A, B](v: F[A])(implicit f: A => B): F[B]
  }

  implicit object ListFunctor extends Functor[List] {
    override def fmap[A, B](v: List[A])(implicit f: A => B): List[B] = v match {
      case Nil => Nil
      case h :: t => f(h) :: fmap(t)(f)
    }
  }

  println(ListFunctor.fmap(List("1", "2", "3")))
}
