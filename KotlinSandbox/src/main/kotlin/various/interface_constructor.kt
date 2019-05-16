package various

interface IConstructor {
    fun bar(i: Int)

    companion object {
        operator fun invoke(f: (Int) -> Unit): IConstructor {
            return object : IConstructor {
                override fun bar(i: Int) = f(i)
            }
        }
    }
}

fun main() {
    println("Hello Kotlin!")
    val foo = IConstructor { println(it) }
    foo.bar(42)
}
