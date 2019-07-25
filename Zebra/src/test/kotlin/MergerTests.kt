import Relations.imRight
import Relations.nextTo
import di.Provider
import model.Constraint
import model.Entry.None
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MergerTests {

    private val merger = Provider().provideMerger()

    @Test
    fun test01() {
        val a = Constraint(1, None, value(33), None, None, rule(imRight, 22), value(2))
        val b = Constraint(1, None, value(33), None, None, rule(imRight, 22), value(2))
        val c = Constraint(1, None, value(33), None, None, rule(nextTo, 22), value(2))
        val d = Constraint(1, value(33), None, None, None, rule(nextTo, 22), value(2))
        assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(c, d)
    }

    @Test
    fun test02() {
        val constraints: Constraints = HashMap()
        arrayOf(
            Constraint(1, value(0), None, rule(imRight, 33), None, None, None),
            Constraint(2, None, value(1), None, None, None, None)
        ).forEach { constraints[it.id] = it }
        val result = hashMapOf(
            1 to Constraint(1, value(0), value(1), rule(imRight, 33), None, None, None)
        )
        merger.mergeConstraints(constraints, 1, 2)

        assertEquals(result, constraints)
    }

    @Test
    fun test03() {
        val constraints: Constraints = HashMap()
        arrayOf(
            Constraint(1, value(0), None, rule(imRight, 33), None, None, None),
            Constraint(2, rule(nextTo, 3), value(1), None, None, None, None),
            Constraint(3, rule(imRight, 33), None, value(4), None, None, value(3))
        ).forEach { constraints[it.id] = it }
        val result = hashMapOf(
            1 to Constraint(1, value(0), None, value(4), None, None, value(3)),
            2 to Constraint(2, rule(nextTo, 1), value(1), None, None, None, None)
        )
        merger.mergeConstraints(constraints, 1, 3)

        assertEquals(result, constraints)
    }


    @Test
    fun test04() {
        fun all(): HashSet<Int> = HashSet(Constraint.defaultVariants)
        val model: Model = HashMap()
        arrayOf(
            1 to arrayListOf(hashSetOf(0, 1), all(), all(), all(), all(), all()),
            2 to arrayListOf(hashSetOf(1, 2), all(), all(), all(), all(), all())
        ).forEach { model[it.first] = it.second }
        val result: Model = HashMap()
        arrayOf(
            1 to arrayListOf(hashSetOf(1), all(), all(), all(), all(), all())
        ).forEach { result[it.first] = it.second }
        merger.mergeModel(model, 1, 2)

        assertEquals(result, model)
    }

    @Test
    fun test05() {
        fun a(): HashSet<Int> = HashSet(Constraint.defaultVariants)
        val model: Model = HashMap()
        arrayOf(
            1 to arrayListOf(hashSetOf(0, 1), a(), a(), a(), a(), a()),
            2 to arrayListOf(hashSetOf(1), hashSetOf(2, 3, 4), a(), a(), a(), a()),
            3 to arrayListOf(hashSetOf(1, 2), a(), a(), a(), a(), a()),
            4 to arrayListOf(a(), a(), a(), a(), hashSetOf(0, 1), a())
        ).forEach { model[it.first] = it.second }
        val result: Model = HashMap()
        arrayOf(
            1 to arrayListOf(hashSetOf(0, 1), a(), a(), a(), a(), a()),
            2 to arrayListOf(hashSetOf(1), hashSetOf(2, 3, 4), a(), a(), hashSetOf(0, 1), a()),
            3 to arrayListOf(hashSetOf(1, 2), a(), a(), a(), a(), a())
        ).forEach { result[it.first] = it.second }
        merger.mergeModel(model, 2, 4)

        assertEquals(result, model)
    }
}
