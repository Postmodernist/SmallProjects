import kotlin.coroutines.experimental.buildSequence

fun numbersCr(): Lazy<Sequence<Int>> = lazy {
  buildSequence {
    yieldAll(listOf(0, 1, 2))
    val numbersSupply = numbersCr().value.iterator()
    while (true)
      yield(numbersSupply.next() + 3)
  }
}

fun main(args: Array<String>) {
  numbersCr().value.take(20).forEach { print("$it ") }
}
