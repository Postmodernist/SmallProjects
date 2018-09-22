
fun fact(n: Int) = { x: Any -> (x as (Any) -> (Int) -> Int)(x)(n) } { x: Any ->
  { n: Int -> if (n == 1) 1 else n * (x as (Any) -> (Int) -> Int)(x)(n - 1) }
}

fun main(args: Array<String>) {
  println(fact(5))
}
