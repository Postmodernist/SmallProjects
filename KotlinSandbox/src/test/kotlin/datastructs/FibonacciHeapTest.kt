package datastructs

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import kotlin.random.Random as R

class FibonacciHeapTest {

    @Test
    fun getSize() {
        val h = FibonacciHeap<Int>()
        assertEquals(0, h.size)
        h.insert(1)
        h.insert(2)
        h.insert(3)
        assertEquals(3, h.size)
    }

    @Test
    fun peek() {
        val h = FibonacciHeap<Int>()
        assertEquals(null, h.peek())
        h.insert(1)
        assertEquals(1, h.peek())
    }

    @Test
    fun extract() {
        val h = FibonacciHeap<Int>()
        var x = h.extract()
        assertEquals(null, x)
        h.insert(1)
        h.insert(3)
        h.insert(2)
        x = h.extract()
        assertEquals(1, x)
        x = h.extract()
        assertEquals(2, x)
        x = h.extract()
        assertEquals(3, x)
        h.insert(1)
        h.insert(1)
        h.extract()
        assertEquals(1, h.peek())
        assertEquals(1, h.size)
    }

    @Test
    fun insert() {
        val h = FibonacciHeap<Int>()
        h.insert(10)
        assertEquals(10, h.peek())
        h.insert(1)
        assertEquals(1, h.peek())
    }

    @Test
    fun remove() {
        val h = FibonacciHeap<Int>()
        assertEquals(false, h.remove(42))
        h.insert(1)
        h.insert(2)
        assertEquals(false, h.remove(42))
        assertEquals(true, h.remove(2))
        assertEquals(1, h.peek())
        assertEquals(1, h.size)
        h.insert(2)
        h.insert(3)
        h.insert(4)
        h.extract()
        assertEquals(2, h.peek())
        assertEquals(3, h.size)
        assertEquals(true, h.remove(3))
        assertEquals(2, h.peek())
        assertEquals(2, h.size)
    }

    @Test
    fun merge() {
        val h = FibonacciHeap<Int>()
        h.insert(12)
        h.insert(5)
        h.insert(8)
        val g = FibonacciHeap<Int>()
        g.insert(1)
        g.insert(2)
        g.insert(42)
        h.merge(g)
        assertEquals(6, h.size)
        assertEquals(0, g.size)
        val a = Array(6) { h.extract() }
        assertArrayEquals(arrayOf(1, 2, 5, 8, 12, 42), a)
    }

    @Test
    fun randomTest() {
        val q = PriorityQueue<Int>()
        val qOut = ArrayList<Int>()
        val h = FibonacciHeap<Int>()
        val hOut = ArrayList<Int>()
        repeat(100) {
            val k = R.nextInt(1000)
            q.offer(k)
            h.insert(k)
        }
        repeat(100) {
            when (R.nextInt(3)) {
                0 -> {
                    val k = R.nextInt(1000)
                    q.offer(k)
                    h.insert(k)
                    assertEquals(q.peek(), h.peek())
                }
                1 -> {
                    val x = q.poll()
                    if (x != null) qOut.add(x)
                    val y = h.extract()
                    if (y != null) hOut.add(y)
                    assertEquals(x, y)
                }
                2 -> {
                    assertEquals(q.size, h.size)
                    if (q.size > 0) {
                        val i = R.nextInt(q.size)
                        val k = q.toArray()[i] as Int
                        assertEquals(q.remove(k), h.remove(k))
                    }
                }
            }
        }
        assertArrayEquals(qOut.toArray(), hOut.toArray())
    }
}
