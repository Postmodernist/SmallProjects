import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import various.getSegmentCount
import various.toHistogram

class SegmentsKtTest {

  private val starts = intArrayOf(1, 3, 10, 3)
  private val ends = intArrayOf(2, 15, 20, 10)

  @Test
  fun toHistogramIsCorrect() {
    assertEquals(mapOf(-1 to 0, 1 to 1, 3 to 2, 10 to 3, 11 to 2, 16 to 1, 21 to 0),
            toHistogram(starts, ends))
  }

  @Test
  fun getSegmentsCountIsCorrect() {
    assertArrayEquals(intArrayOf(0, 1, 1, 2, 2, 3, 2, 1, 0),
            getSegmentCount(starts, ends, intArrayOf(0, 1, 2, 3, 4, 10, 15, 20, 25)))
  }
}
