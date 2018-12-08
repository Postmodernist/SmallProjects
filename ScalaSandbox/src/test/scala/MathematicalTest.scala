import org.scalatest.FunSuite

class MathematicalTest extends FunSuite {
    test("Mathematical.cube") {
        assert(Mathematical.cube(3) === 27)
        assert(Mathematical.cube(0) === 0)
    }

    test("Mathematical.sqrt") {
        assert(Math.abs(Mathematical.sqrt(2) - 1.4142135623730951) < 0.001)
    }

    test("Mathematical.factorial") {
        assert(Mathematical.factorial(0) === 1)
        assert(Mathematical.factorial(10) === 3628800)
    }

    test("Mathematical.fact") {
        assert(Mathematical.fact(0) === 1)
        assert(Mathematical.fact(10) === 3628800)
    }

    test("Mathematical.gcd") {
        assert(Mathematical.gcd(14, 21) === 7)
    }
}
