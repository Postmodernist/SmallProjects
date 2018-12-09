case class Rational(var numer: Int, var denom: Int) {
  require(denom > 0, "Denominator must be positive.") // throws IllegalArgumentException

  private val g = gcd(numer, denom)
  numer = numer / g
  denom = denom / g

  def this(numer: Int) = this(numer, 1) // auxiliary constructor

  def +(r: Rational) = Rational(numer * r.denom + r.numer * denom, denom * r.denom)

  def -(r: Rational) = Rational(numer * r.denom - r.numer * denom, denom * r.denom)

  def *(r: Rational) = Rational(numer * r.numer, denom * r.denom)

  def /(r: Rational) = Rational(numer * r.denom, denom * r.numer)

  def max(r: Rational): Rational = if (this < r) r else this

  def <(r: Rational): Boolean = numer * r.denom < r.numer * denom

  override def toString: String = s"$numer/$denom"

  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
}

object ObjectOriented extends App {

  val x = Rational(1, 3)
  val y = Rational(5, 7)
  val z = Rational(3, 2)

  println((x + y) * z)
}
