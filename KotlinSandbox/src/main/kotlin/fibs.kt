import kotlin.coroutines.experimental.buildSequence

fun main(args: Array<String>) {
  val fib: Sequence<Int> = buildSequence {
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
