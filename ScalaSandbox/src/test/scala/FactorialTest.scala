import org.scalatest.FunSuite

class FactorialTest extends FunSuite {
    test("Factorial.factorial") {
        assert(Factorial.factorial(0) === 1)
        assert(Factorial.factorial(10) === 3628800)
    }

    test("Factorial.fact") {
        assert(Factorial.fact(0) === 1)
        assert(Factorial.fact(10) === 3628800)
    }
}
