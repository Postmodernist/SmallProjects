object Typeclasses extends App {

  class Rational(x: Int, y: Int) {

    lazy val numer: Int = x / g
    lazy val denom: Int = y / g
    private val g = gcd(x, y)

    override def toString: String = s"$numer/$denom"

    private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  }

  val nums = List(4, 2, -5, 6, 0)
  val fruit = List("pear", "orange", "apple", "pineapple")
  val half = new Rational(1, 2)
  val third = new Rational(1, 3)
  val fourth = new Rational(1, 4)
  val rationals = List(third, half, fourth)

  val cmpRational: (Rational, Rational) => Int = (x, y) => x.numer * y.denom - y.numer * x.denom
  implicit val rationalOrder: Ordering[Rational] = cmpRational(_, _)

  println(insertionSort(nums))
  println(insertionSort(fruit))
  println(insertionSort(rationals))

  def insertionSort[T](xs: List[T])(implicit ord: Ordering[T]): List[T] = {
    def insert(y: T, ys: List[T]): List[T] =
      ys match {
        case List() => y :: List()
        case z :: zs =>
          if (ord.lt(y, z)) y :: z :: zs
          else z :: insert(y, zs)
      }

    xs match {
      case List() => List()
      case y :: ys => insert(y, insertionSort(ys))
    }
  }
}
