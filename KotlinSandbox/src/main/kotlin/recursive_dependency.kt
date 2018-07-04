import kotlin.coroutines.experimental.buildSequence

fun seq(): Sequence<Int> = buildSequence {
  yieldAll(0..3)
  val x = { seq() }
  yieldAll(x())
}

fun main(args: Array<String>) {
  seq().take(10).forEach { println(it) }
}
