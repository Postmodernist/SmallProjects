package various

object InterfaceConstructor {

    interface Foo {
        fun bar(i: Int)

        companion object {
            operator fun invoke(f: (Int) -> Unit): Foo {
                return object : Foo {
                    override fun bar(i: Int) = f(i)
                }
            }
        }
    }

    fun main() {
        println("Hello Kotlin!")
        val foo = Foo { println(it) }
    }
}
