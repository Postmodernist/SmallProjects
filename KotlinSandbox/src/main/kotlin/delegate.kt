interface Foo {
    fun bar(a: Int)
}

class FooDelegate : Foo {
    override fun bar(a: Int) {
        println("From FooDelegate: $a")
    }
}

class Qux(val a: Int = 10) : Foo by FooDelegate()

fun main(args: Array<String>) {
    val qux = Qux()
    qux.bar(qux.a)
}
