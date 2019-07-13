package various

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import various.Colors.*
import various.Drinks.WATER
import various.Merger.Constraint
import various.Merger.Entry.None
import various.Merger.Entry.Value
import various.Pets.DOG
import various.Pets.SNAILS

class ZebraKtTest {

    @Test
    fun testRules01() {
        val merger = Merger().apply {
            // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
            add(Constraint(104, Value(3), value(RED), None, None, None, None))
            add(Constraint(105, rule(::imRight, 104), value(GREEN), None, None, None, None))
            add(Constraint(106, rule(::nextTo, 104), value(IVORY), None, None, None, None))
        }.merge()
        for (c in merger.constraints) {
            println(c.show())
        }

        val expected = arrayOf(
                arrayOf(2, IVORY.ordinal, -1, -1, -1, -1),
                arrayOf(3, RED.ordinal, -1, -1, -1, -1),
                arrayOf(4, GREEN.ordinal, -1, -1, -1, -1)
        )
        val result = merger.constraints.map {
            it.entries.map { e -> if (e is Value) e.v else -1 }.toTypedArray()
        }.sortedBy { it[0] }.toTypedArray()
        assertArrayEquals(expected, result)
    }

    @Test
    fun testRules02() {
        val merger = Merger().apply {
            // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
            add(Constraint(104, Value(3), value(RED), None, value(DOG), None, None))
            add(Constraint(105, rule(::imRight, 104), value(GREEN), None, None, None, None))
            add(Constraint(106, None, None, None, value(SNAILS), value(WATER), None))
        }.merge()
        for (c in merger.constraints) {
            println(c.show())
        }

        val expected = arrayOf(
                arrayOf(3, RED.ordinal, -1, DOG.ordinal, -1, -1),
                arrayOf(4, GREEN.ordinal, -1, SNAILS.ordinal, WATER.ordinal, -1)
        )
        val result = merger.constraints.map {
            it.entries.map { e -> if (e is Value) e.v else -1 }.toTypedArray()
        }.sortedBy { it[0] }.toTypedArray()
        assertArrayEquals(expected, result)
    }
}