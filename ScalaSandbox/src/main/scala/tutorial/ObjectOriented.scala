package tutorial

case class Rational(numer: Int, denom: Int) {
  require(denom > 0, "Denominator must be positive.") // throws IllegalArgumentException

  def this(numer: Int) = this(numer, 1) // auxiliary constructor

  def +(r: Rational) = Rational(numer * r.denom + r.numer * denom, denom * r.denom)

  def -(r: Rational) = Rational(numer * r.denom - r.numer * denom, denom * r.denom)

  def *(r: Rational) = Rational(numer * r.numer, denom * r.denom)

  def /(r: Rational) = Rational(numer * r.denom, denom * r.numer)

  def max(r: Rational): Rational = if (this < r) r else this

  def <(r: Rational): Boolean = numer * r.denom < r.numer * denom

  override def toString: String = s"$numer/$denom"

}

object Rational {
  def apply(numer: Int, denom: Int): Rational = {
    val g = gcd(numer, denom)
    new Rational(numer / g, denom / g)
  }

  def unapply(rational: Rational): Option[(Int, Int)] =
    if (rational eq null) None
    else Some((rational.numer, rational.denom))

  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
}

object ObjectOriented extends App {

  val x = Rational(1, 3)
  val y = Rational(5, 7)
  val z = Rational(3, 2)

  println((x + y) * z)
}
