package algorithms

import algorithms.Knapsack.{knapsackRepR, knapsackRep, knapsackR, knapsack, knapsackBf}
import org.scalatest.FunSuite

class KnapsackTest extends FunSuite {
  test("Knapsack.knapsackRepR") {
    assert(knapsackRepR(ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9), u = 10) === 48)
  }

  test("Knapsack.knapsackRep") {
    assert(knapsackRep(W = 10, ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9)) === 48)
  }

  test("Knapsack.knapsackR") {
    assert(knapsackR(ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9), u = 10, i = 4) === 46)
  }

  test("Knapsack.knapsack") {
    assert(knapsack(W = 10, ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9)) === 46)
  }

  test("Knapsack.knapsackBf") {
    assert(knapsackBf(W = 10, ws = Array(6, 3, 4, 2), vs = Array(30, 14, 16, 9)) === 46)
  }
}
