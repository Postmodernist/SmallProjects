import org.junit.Assert.assertArrayEquals
import org.junit.Test
import Cigarettes.CHESTERFIELDS
import Colors.*
import Drinks.*
import model.Entry.*
import Nations.*
import Pets.*
import Relations.imRight
import Relations.nextTo
import di.Provider
import model.Constraint

class SimplifierTests {

    @Test
    fun test01() = makeTest("Test 01") {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
            Constraint(100, value(3), value(RED), None, None, None, None),
            Constraint(101, rule(imRight, 100), value(GREEN), None, None, None, None),
            Constraint(102, rule(nextTo, 100), value(IVORY), None, None, None, None)
        )
        expected(
            arrayOf(3, RED.ordinal, -1, -1, -1, -1),
            arrayOf(4, GREEN.ordinal, -1, -1, -1, -1),
            arrayOf(2, IVORY.ordinal, -1, -1, -1, -1)
        )
    }

    @Test
    fun test02() = makeTest("Test 02") {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
            Constraint(100, value(3), value(RED), None, value(DOG), None, None),
            Constraint(101, rule(imRight, 100), value(GREEN), None, None, None, None),
            Constraint(102, None, None, None, value(SNAILS), value(WATER), None)
        )
        expected(
            arrayOf(3, RED.ordinal, -1, DOG.ordinal, -1, -1),
            arrayOf(4, GREEN.ordinal, -1, -1, -1, -1),
            arrayOf(-1, -1, -1, SNAILS.ordinal, WATER.ordinal, -1)
        )
    }

    @Test
    fun test03() = makeTest("Test 03") {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
            Constraint(100, value(2), value(RED), None, None, None, None),
            Constraint(101, rule(imRight, 100), None, value(ENGLISHMAN), None, None, None),
            Constraint(102, rule(nextTo, 101), value(GREEN), value(JAPANESE), None, None, None)
        )
        expected(
            arrayOf(2, RED.ordinal, -1, -1, -1, -1),
            arrayOf(3, -1, ENGLISHMAN.ordinal, -1, -1, -1),
            arrayOf(4, GREEN.ordinal, JAPANESE.ordinal, -1, -1, -1)
        )
    }

    @Test
    fun test04() = makeTest("Test 04") {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
            Constraint(100, value(2), value(RED), None, None, None, None),
            Constraint(101, rule(nextTo, 102), None, value(ENGLISHMAN), None, None, None),
            Constraint(102, None, None, None, None, value(WATER), None)
        )
        expected(
            arrayOf(2, RED.ordinal, -1, -1, -1, -1),
            arrayOf(-1, -1, ENGLISHMAN.ordinal, -1, -1, -1),
            arrayOf(-1, -1, -1, -1, WATER.ordinal, -1)
        )
    }

    @Test
    fun test05() = makeTest("Test 05") {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
            Constraint(100, value(2), value(RED), value(SPANIARD), None, None, None),
            Constraint(101, rule(imRight, 102), None, value(ENGLISHMAN), None, None, None),
            Constraint(102, None, value(BLUE), None, None, value(WATER), None),
            Constraint(103, value(1), None, value(NORWEGIAN), None, None, None),
            Constraint(104, value(0), None, None, None, value(COFFEE), None)
        )
        expected(
            arrayOf(2, RED.ordinal, SPANIARD.ordinal, -1, -1, -1),
            arrayOf(4, -1, ENGLISHMAN.ordinal, -1, -1, -1),
            arrayOf(3, BLUE.ordinal, -1, -1, WATER.ordinal, -1),
            arrayOf(1, -1, NORWEGIAN.ordinal, -1, -1, -1),
            arrayOf(0, -1, -1, -1, COFFEE.ordinal, -1)
        )
    }

    @Test
    fun test06() = makeTest("Test 06") {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
            Constraint(100, None, value(IVORY), None, None, None, None),
            Constraint(101, rule(imRight, 100), value(GREEN), None, None, None, None),
            Constraint(102, Value(2), value(RED), None, None, value(MILK), None),
            Constraint(103, Value(0), None, value(NORWEGIAN), None, None, None),
            Constraint(104, None, value(BLUE), None, None, None, None),
            Constraint(105, rule(nextTo, 104), None, value(NORWEGIAN), None, None, None)
        )
        expected(
            arrayOf(3, IVORY.ordinal, -1, -1, -1, -1),
            arrayOf(4, GREEN.ordinal, -1, -1, -1, -1),
            arrayOf(2, RED.ordinal, -1, -1, MILK.ordinal, -1),
            arrayOf(0, -1, NORWEGIAN.ordinal, -1, -1, -1),
            arrayOf(1, BLUE.ordinal, -1, -1, -1, -1)
        )
    }

    @Test
    fun test07() = makeTest("Test 07") {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
            Constraint(100, value(0), value(IVORY), None, None, None, None),
            Constraint(101, rule(imRight, 100), None, value(ENGLISHMAN), None, None, None),
            Constraint(102, rule(imRight, 101), None, None, value(ZEBRA), None, None),
            Constraint(103, rule(imRight, 102), None, None, None, value(COFFEE), None),
            Constraint(104, rule(imRight, 103), None, None, None, None, value(CHESTERFIELDS))
        )
        expected(
            arrayOf(0, IVORY.ordinal, -1, -1, -1, -1),
            arrayOf(1, -1, ENGLISHMAN.ordinal, -1, -1, -1),
            arrayOf(2, -1, -1, ZEBRA.ordinal, -1, -1),
            arrayOf(3, -1, -1, -1, COFFEE.ordinal, -1),
            arrayOf(4, -1, -1, -1, -1, CHESTERFIELDS.ordinal)
        )
    }

    @Test
    fun test08() = makeTest("Test 08") {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
            Constraint(100, value(0), value(IVORY), None, None, None, None),
            Constraint(101, rule(nextTo, 100), None, value(ENGLISHMAN), None, None, None),
            Constraint(102, rule(nextTo, 101), None, None, value(ZEBRA), None, None),
            Constraint(103, rule(nextTo, 102), None, None, None, value(COFFEE), None),
            Constraint(104, rule(nextTo, 103), None, None, None, None, value(CHESTERFIELDS))
        )
        expected(
            arrayOf(0, IVORY.ordinal, -1, -1, -1, -1),
            arrayOf(1, -1, ENGLISHMAN.ordinal, -1, -1, -1),
            arrayOf(-1, -1, -1, ZEBRA.ordinal, -1, -1),
            arrayOf(-1, -1, -1, -1, COFFEE.ordinal, -1),
            arrayOf(-1, -1, -1, -1, -1, CHESTERFIELDS.ordinal)
        )
    }

    private fun makeTest(title: String, block: TestData.() -> Unit) {
        println("=== $title ===\n")
        val testData = TestData().apply { block() }
        val simplifier = Provider().provideSimplifier().apply {
            testData.constraints.forEach { add(it) }
        }.simplify()
        simplifier.constraints.forEach { (_, c) -> println(c.show()) }
        println()
        val result = simplifier.constraints.map { (_, c) ->
            c.entries.map { e -> if (e is Value) e.v else -1 }.toTypedArray()
        }.toTypedArray()
        assertArrayEquals(testData.expected, result)
    }

    class TestData {
        lateinit var constraints: Array<Constraint>
        lateinit var expected: Array<Array<Int>>

        fun constraints(vararg args: Constraint) {
            constraints = arrayOf(*args)
        }

        fun expected(vararg args: Array<Int>) {
            expected = arrayOf(*args)
        }
    }
}
