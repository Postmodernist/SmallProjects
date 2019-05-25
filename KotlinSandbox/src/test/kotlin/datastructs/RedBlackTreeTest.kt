package datastructs

import org.junit.Assert.*
import org.junit.Test
import java.util.*
import kotlin.random.Random

class RedBlackTreeTest {

    @Test
    fun random() {
        val a = ArrayList<Int>()
        val x = TreeSet<Int>()
        val y = RedBlackTree<Int>()
        repeat(1000) {
            val k = Random.nextInt()
            a.add(k)
            x.add(k)
            y.insert(k)
        }
        repeat(100_000) {
            if (a.isEmpty() || Random.nextBoolean()) {
                val k = Random.nextInt()
                a.add(k)
                x.add(k)
                y.insert(k)
            } else {
                val i = Random.nextInt(a.size)
                val k = a[i]
                a.removeAt(i)
                x.remove(k)
                y.remove(k)
            }
            assertArrayEquals(x.toIntArray(), y.toList().toIntArray())
        }
    }
}