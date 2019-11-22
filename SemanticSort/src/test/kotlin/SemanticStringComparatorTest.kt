import org.junit.Assert.assertTrue
import org.junit.Test

class SemanticStringComparatorTest {

    private val comparator = SemanticStringComparator()

    @Test
    fun test01() {
        assertEqual(
            "Monitor-4.1.8",
            "Monitor-4.1.8"
        )
    }

    @Test
    fun test02() {
        assertEqual(
            "Monitor-4.1.8-dev-10-g316cd31",
            "Monitor-4.1.8-dev-10-g316cd31"
        )
    }

    @Test
    fun test03() {
        assertLess(
            "Monitor-4.1.8",
            "Monitor-4.1.10"
        )
    }

    @Test
    fun test04() {
        assertLess(
            "Monitor-4.1.8",
            "Monitor-4.1.8.1"
        )
    }

    @Test
    fun test05() {
        assertLess(
            "Monitor-4.1.8",
            "Monitor-4.1.8-dev"
        )
    }

    @Test
    fun test06() {
        assertLess(
            "Monitor-4.1.8",
            "Monitor-4.1.8-dev-10-g316cd31"
        )
    }

    @Test
    fun test07() {
        assertLess(
            "Monitor-4.1.8-dev",
            "Monitor-4.1.8-dev-10-g316cd31"
        )
    }

    @Test
    fun test08() {
        assertLess(
            "Monitor-4.1.8-dev-10-g316cd31",
            "Monitor-4.1.8-dev-15-g316cd31"
        )
    }

    @Test
    fun test09() {
        assertGreater(
            "Monitor-4.1.8-dev-100-g316cd31",
            "Monitor-4.1.8-dev-15-g316cd31"
        )
    }

    @Test
    fun test10() {
        assertGreater(
            "Monitor-4.1.8-rc",
            "Monitor-4.1.8-dev-15-g316cd31"
        )
    }

    @Test
    fun test11() {
        assertLess(
            "Monitor-4.8.100",
            "Monitor-4.10.1"
        )
    }

    @Test
    fun test12() {
        assertLess(
            "Monitor-4.8.9999999999999",
            "Monitor-4.9.1"
        )
    }

    @Test
    fun test13() {
        assertGreater(
            "Monitor-9",
            "Monitor-8.11111111.9999999999999"
        )
    }

    @Test
    fun test14() {
        assertEqual(
            null,
            null
        )
    }

    @Test
    fun test15() {
        assertGreater(
            "Monitor-9",
            null
        )
    }

    @Test
    fun test16() {
        assertLess(
            null,
            "Monitor-8.11111111.9999999999999"
        )
    }

    private fun assertLess(first: String?, second: String?) {
        assertTrue(comparator.compare(first, second) == -1)
    }

    private fun assertGreater(first: String?, second: String?) {
        assertTrue(comparator.compare(first, second) == 1)
    }

    private fun assertEqual(first: String?, second: String?) {
        assertTrue(comparator.compare(first, second) == 0)
    }
}