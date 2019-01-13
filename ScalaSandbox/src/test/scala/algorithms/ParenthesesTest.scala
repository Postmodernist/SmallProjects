package algorithms

import algorithms.Parentheses.parentheses
import org.scalatest.{BeforeAndAfter, FunSuite}

class ParenthesesTest extends FunSuite with BeforeAndAfter {
  val add: (Int, Int) => Int = _ + _
  val sub: (Int, Int) => Int = _ - _
  val mul: (Int, Int) => Int = _ * _

  test("Parentheses.parentheses") {
    assert(parentheses(Array(1, 5), List(add)) === 6)
    assert(parentheses(Array(5, 8, 7, 4, 8, 9), List(sub, add, mul, sub, add)) === 200)
  }
}
