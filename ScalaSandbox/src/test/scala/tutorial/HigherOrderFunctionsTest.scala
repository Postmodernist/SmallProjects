package tutorial

import org.scalatest.FunSuite

class HigherOrderFunctionsTest extends FunSuite {
    test("HigherOrderFunctions.sumInts") {
        assert(HigherOrderFunctions.sumInts(1, 3) === 6)
    }

    test("HigherOrderFunctions.sumCubes") {
        assert(HigherOrderFunctions.sumCubes(1, 3) === 36)
    }

    test("HigherOrderFunctions.sumFactorials") {
        assert(HigherOrderFunctions.sumFactorials(1, 3) === 9)
    }
}
