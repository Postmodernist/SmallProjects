import di.Provider
import model.Constraint
import model.Entry.None
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.*

class MatcherTests {

    private val matcher = Provider().provideMatcher()

    @Test
    fun test01() {
        val constraints: Constraints = TreeMap()
        arrayOf(
            Constraint(1, value(1), None, None, None, None, None),
            Constraint(2, value(1), None, None, None, None, None)
        ).forEach { constraints[it.id] = it }

        assertEquals(Pair(1, 2), matcher.findMatch(constraints))
    }

    @Test
    fun test02() {
        val constraints: Constraints = TreeMap()
        arrayOf(
            Constraint(1, value(1), None, None, None, None, None),
            Constraint(2, value(2), None, None, None, None, None)
        ).forEach { constraints[it.id] = it }

        assertNull(matcher.findMatch(constraints))
    }

    @Test
    fun test03() {
        val constraints: Constraints = TreeMap()
        arrayOf(
            Constraint(1, value(5), None, None, None, None, None),
            Constraint(2, None, None, None, None, value(5), None),
            Constraint(3, value(2), None, None, None, None, None),
            Constraint(4, None, value(2), None, None, value(5), None)
        ).forEach { constraints[it.id] = it }

        assertEquals(Pair(2, 4), matcher.findMatch(constraints))
    }

    @Test
    fun test04() {
        val constraints: Constraints = TreeMap()
        arrayOf(
            Constraint(1, value(5), value(2), None, None, None, None),
            Constraint(2, None, None, None, None, value(5), None),
            Constraint(3, value(2), None, None, None, None, None),
            Constraint(4, None, value(2), None, None, value(5), None)
        ).forEach { constraints[it.id] = it }

        assertEquals(4, matcher.findMatch(constraints, 1, 1, 2))
        assertEquals(2, matcher.findMatch(constraints, 4, 4, 5))
    }
}