import org.scalatest.FunSuite

class SquareRootTest extends FunSuite {
    test("SquareRoot.sqrt") {
        assert(Math.abs(SquareRoot.sqrt(2) - 1.4142135623730951) < 0.001)
    }
}
