interface IFact {
    operator fun invoke(self: IFact, n: Int): Int
}

fun fact(n: Int) = { self: IFact -> self(self, n) }(object : IFact {
    override fun invoke(self: IFact, n: Int): Int = if (n == 1) 1 else n * self(self, n - 1)
})

fun main(args: Array<String>) {
    assert(fact(5) == 120)
    assert(fact(8) == 40320)
    println("OK")
}
