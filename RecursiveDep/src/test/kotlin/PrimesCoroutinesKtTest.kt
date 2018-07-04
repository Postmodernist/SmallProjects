import org.junit.Assert.*
import org.junit.Test

class PrimesCoroutinesKtTest {
  @Test
  fun generateFirst25Primes() {
    val expecteds = arrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97).toIntArray()
    val actuals = primesCr().take(25).toList().toIntArray()
    assertArrayEquals("Failed to generate valid primes", expecteds, actuals)
  }

  @Test
  fun generate1000thPrime() {
    val actual = primesCr().drop(999).first()
    assertEquals("Wrong number generated", 7919, actual)
  }
}
