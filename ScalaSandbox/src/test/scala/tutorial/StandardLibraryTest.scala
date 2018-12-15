package tutorial

import org.scalatest.FunSuite

class StandardLibraryTest extends FunSuite {
    test("StandardLibrary.insertionSort") {
        val l1 = List(3, 1, 2)
        val l2 = List(3, 2, 1)
        val l3 = List(1, 2, 3)

        val expected = List(1, 2, 3)

        assert(StandardLibrary.insertionSort(l1) === expected)
        assert(StandardLibrary.insertionSort(l2) === expected)
        assert(StandardLibrary.insertionSort(l3) === expected)
    }

    test("StandardLibrary.sqrtOption") {
        assert(StandardLibrary.sqrtOption(4.0) === "2.0")
        assert(StandardLibrary.sqrtOption(-1.0) === "No value")
    }

    test("StandardLibrary.sqrtTry") {
        assert(StandardLibrary.sqrtTry(4.0) === "2.0")
        assert(StandardLibrary.sqrtTry(-1.0) === "x must be positive")
    }

    test("StandardLibrary.sqrtEither") {
        assert(StandardLibrary.sqrtEither(4.0) === "2.0")
        assert(StandardLibrary.sqrtEither(-1.0) === "x must be positive")
    }
}
