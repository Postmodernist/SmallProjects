package various

import java.util.*

fun toHistogram(starts: IntArray, ends: IntArray): Map<Int, Int> {
  val steps = mutableListOf<Pair<Int, Int>>()
  steps += starts.zip(IntArray(starts.size) { 1 })
  steps += ends.map { it + 1 }.zip(List(ends.size) { -1 })
  steps.sortBy { it.first }
  val histogram = mutableMapOf(-1 to 0)
  steps.fold(0) { acc, i ->
    val next = acc + i.second
    histogram[i.first] = next
    next
  }
  return histogram
}

fun getSegmentCount(starts: IntArray, ends: IntArray, points: IntArray): IntArray {
  val histogram = toHistogram(starts, ends)
  val keys = histogram.keys.toIntArray()
  return IntArray(points.size) {
    val n = points[it]
    val i = keys.binarySearch(n).let { if (it < 0) it.inv() - 1 else it }
    histogram[keys[i]]!!
  }
}

fun main() {
  val starts = intArrayOf(1, 3, 10, 3)
  val ends = intArrayOf(2, 15, 20, 10)
  println(starts.zip(ends).sortedWith(compareBy({ it.first }, { it.second })))
  val histogram = toHistogram(starts, ends)
  println(histogram)

  val scanner = StringTokenizer(readLine())
  val points = mutableListOf<Int>()
  while (scanner.hasMoreTokens()) {
    points += scanner.nextToken()!!.toInt()
  }
  println(getSegmentCount(starts, ends, points.toIntArray()).toList())
}