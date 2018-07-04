typealias Action = () -> Unit

fun test1() {
  val fis = mutableListOf<Action>()
  for (i in 0..4) {
    fis.add { println(i) }
  }
  for (fi in fis) {
    fi()
  }
}

fun test2() {
  val fis = mutableListOf<Action>()
  var outer: Int
  for (i in 0..4) {
    outer = i
    fis.add { println(outer) }
  }
  for (fi in fis) {
    fi()
  }
}

fun test3() {
  val fis = mutableListOf<Action>()
  for (i in 0..4) {
    val inner: Int = i
    fis.add { println(inner) }
  }
  for (fi in fis) {
    fi()
  }
}

fun main(args: Array<String>) {
  test1()
  println()
  test2()
  println()
  test3()
}
