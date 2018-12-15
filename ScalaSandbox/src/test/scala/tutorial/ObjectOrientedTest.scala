package tutorial

import org.scalatest.FunSuite

class ObjectOrientedTest extends FunSuite {
  test("ObjectOriented.Rational") {
    val x = Rational(1, 3)
    val y = Rational(5, 7)
    val z = Rational(3, 2)

    assert(x.+(y).*(z) == Rational(11, 7))
  }
}
