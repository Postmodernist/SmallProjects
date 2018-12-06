object Factorial extends App {
    def factorial(n: Int): Int =
        if (n == 0) 1
        else factorial(n - 1) * n

    def fact(n: Int) = ((x: Y) => x.f(x, n)) ((x, n) => if (n == 0) 1 else n * x.f(x, n - 1))

    trait Y {
        def f(x: Y, n: Int): Int
    }

    println(factorial(10))
    println(fact(10))
}
