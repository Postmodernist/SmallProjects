package algorithms

import algorithms.MatrixMult.{matrixMultR, matrixMult}
import org.scalatest.FunSuite

class MatrixMultTest extends FunSuite {
  test("MatrixMult.matrixMultR") {
    assert(matrixMultR(m = Array(50, 20, 1, 10, 100), i = 0, j = 4) === 7000)
  }

  test("MatrixMult.matrixMult") {
    assert(matrixMult(m = Array(50, 20, 1, 10, 100)) === 7000)
  }
}
