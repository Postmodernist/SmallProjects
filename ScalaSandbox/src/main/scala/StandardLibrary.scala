import scala.util.{Failure, Success, Try}

object StandardLibrary extends App {
  //noinspection ScalaUnusedSymbol
  def lists(): Unit = {
    val fruit: List[String] = List("apples", "oranges", "pears")
    val ints: List[Int] = 1 :: 2 :: 3 :: 4 :: Nil
    val empty: List[Nothing] = Nil

    val intsAddAll = List(-1, 0) ::: ints

    println(intsAddAll)

    val l = (1 to 5).toList
    val lMap = l.map(x => x + 1)
    val lFilter = l.filter(x => x % 2 == 0)
    val lFlatMap = l.flatMap { x =>
      List(x, x * 2, x * 3)
    }

    println(lMap)
    println(lFilter)
    println(lFlatMap)
  }

  lists()

  def insertionSort(xs: List[Int]): List[Int] = {
    val cond: (Int, Int) => Boolean = (a, b) => a <= b

    def insert(x: Int, xs: List[Int]): List[Int] = xs match {
      case Nil => x :: Nil
      case head :: tl =>
        if (cond(x, head)) x :: head :: tl
        else head :: insert(x, tl)
    }

    xs match {
      case Nil => Nil
      case head :: tl => insert(head, insertionSort(tl))
    }
  }

  def sqrtOption(x: Double): String = {
    def sqrt(x: Double): Option[Double] =
      if (x < 0) None
      else Some(Math.sqrt(x))

    sqrt(x) match {
      case Some(value) => value.toString
      case None => "No value"
    }
  }

  def sqrtTry(x: Double): String = {
    def sqrt(x: Double): Try[Double] =
      if (x < 0) Failure(new IllegalArgumentException("x must be positive"))
      else Success(Math.sqrt(x))

    sqrt(x) match {
      case Failure(exception) => exception.getMessage
      case Success(value) => value.toString
    }
  }

  def sqrtEither(x: Double): String = {
    def sqrt(x: Double): Either[String, Double] =
      if (x < 0) Left("x must be positive")
      else Right(Math.sqrt(x))

    sqrt(x) match {
      case Left(value) => value
      case Right(value) => value.toString
    }
  }
}
