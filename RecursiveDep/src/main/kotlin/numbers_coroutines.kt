fun numbersCr(): Sequence<Int> = sequence {
  yieldAll(0..2)
  val numbersSupply = numbersCr().iterator()
  while (true)
    yield(numbersSupply.next() + 3)
}

fun main(args: Array<String>) {
  numbersCr().take(20).forEach { print("$it ") }
}
