import org.scalatest.FunSuite

class StructuresTest extends FunSuite {
    test("Structures.symbolDuration") {
        val symbol1: Symbol = Note(C, Quarter, 3)
        val symbol2: Symbol = Rest(Whole)

        assert(Structures.symbolDuration(symbol1) === Quarter)
        assert(Structures.symbolDuration(symbol2) === Whole)
    }

    test("Structures_Equality") {
        val c3 = Note(C, Quarter, 3)
        val otherC3 = Note(C, Quarter, 3)
        val f3 = Note(F, Quarter, 3)

        assert((c3 == otherC3) === true)
        assert((c3 == f3) === false)
    }

    test("Structures.fractionOfWhole") {
        assert(Structures.fractionOfWhole(Half) === 0.5)
        assert(Structures.fractionOfWhole(Quarter) === 0.25)
    }
}
