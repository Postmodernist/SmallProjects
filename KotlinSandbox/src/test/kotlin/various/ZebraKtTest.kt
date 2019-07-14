package various

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import various.Colors.*
import various.Cigarettes.*
import various.Drinks.*
import various.Merger.Constraint
import various.Merger.Entry.None
import various.Merger.Entry.Value
import various.Nations.*
import various.Pets.*
import various.Relations.imRight
import various.Relations.nextTo

class ZebraKtTest {

    @Test
    fun testRules01() = makeTest {
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
    fun testRules02() = makeTest {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
                Constraint(100, value(3), value(RED), None, value(DOG), None, None),
                Constraint(101, rule(imRight, 100), value(GREEN), None, None, None, None),
                Constraint(102, None, None, None, value(SNAILS), value(WATER), None)
        )
        expected(
                arrayOf(3, RED.ordinal, -1, DOG.ordinal, -1, -1),
                arrayOf(4, GREEN.ordinal, -1, SNAILS.ordinal, WATER.ordinal, -1)
        )
    }

    @Test
    fun testRules03() = makeTest {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
                Constraint(100, value(2), value(RED), None, None, None, None),
                Constraint(101, rule(imRight, 100), None, value(ENGLISHMAN), None, None, None),
                Constraint(102, rule(nextTo, 101), None, value(JAPANESE), None, value(WATER), None)
        )
        expected(
                arrayOf(2, RED.ordinal, -1, -1, -1, -1),
                arrayOf(3, -1, ENGLISHMAN.ordinal, -1, -1, -1),
                arrayOf(4, -1, JAPANESE.ordinal, -1, WATER.ordinal, -1)
        )
    }

    @Test
    fun testRules04() = makeTest {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
                Constraint(100, value(2), value(RED), None, None, None, None),
                Constraint(101, rule(nextTo, 102), None, value(ENGLISHMAN), None, None, None),
                Constraint(102, None, None, None, None, value(WATER), None)
        )
        // Both 101 and 102 match 100, and don't match each other, so no merging.
        expected(
                arrayOf(2, RED.ordinal, -1, -1, -1, -1),
                arrayOf(-1, -1, ENGLISHMAN.ordinal, -1, -1, -1),
                arrayOf(-1, -1, -1, -1, WATER.ordinal, -1)
        )
    }

    @Test
    fun testRules05() = makeTest {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        constraints(
                Constraint(100, value(2), value(RED), None, None, None, None),
                Constraint(101, rule(nextTo, 102), None, value(ENGLISHMAN), None, None, None),
                Constraint(102, None, value(BLUE), None, None, value(WATER), None)
        )
        // The only match is 100 and 101.
        expected(
                arrayOf(2, RED.ordinal, ENGLISHMAN.ordinal, -1, -1, -1),
                arrayOf(-1, BLUE.ordinal, -1, -1, WATER.ordinal, -1)
        )
    }

    @Test
    fun testRules06() = makeTest {
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

    private fun makeTest(block: TestData.() -> Unit) {
        val testData = TestData().apply { block() }
        val merger = Merger().apply { testData.constraints.forEach { add(it) } }.merge()
        merger.constraints.forEach { println(it.show()) }
        val result = merger.constraints.map {
            it.entries.map { e -> if (e is Value) e.v else -1 }.toTypedArray()
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