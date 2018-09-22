
/*
fun <T : (T) -> (Int) -> Int> fact(n: Int) = { x: T -> x(x)(n) } { x: T ->
  { n: Int -> if (n == 1) 1 else n * x(x)(n - 1) }
}

fun main(args: Array<String>) {
  println(fact(5))
}
*/
