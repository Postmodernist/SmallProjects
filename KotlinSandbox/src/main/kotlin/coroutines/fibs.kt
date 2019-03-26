package coroutines

fun main() {
  val fib: Sequence<Int> = sequence {
    var a = 1
    var b = 1

    while (true) {
      yield(a)
      val tmp = a
      a = b
      b += tmp
    }
  }

  fib.take(10).forEach { println(it) }
}
