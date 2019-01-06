package algorithms

import algorithms.EditDistance.{editDistanceR, editDistance}
import org.scalatest.FunSuite

class EditDistanceTest extends FunSuite {
  test("EditDistance.editDistanceR") {
    assert(editDistanceR(a = "editing", b = "distance", i = 7, j = 8) === 5)
  }

  test("EditDistance.editDistance") {
    assert(editDistance(a = "editing", b = "distance") === 5)
  }
}
