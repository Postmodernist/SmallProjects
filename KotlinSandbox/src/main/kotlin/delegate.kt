interface Base {
    fun method(a: Int)
}

class BaseDelegate : Base {
    override fun method(a: Int) {
        println("From BaseDelegate: $a")
    }
}

class Derived(val a: Int = 10) : Base by BaseDelegate()

fun main(args: Array<String>) {
    val derived = Derived()
    derived.method(derived.a)
}
