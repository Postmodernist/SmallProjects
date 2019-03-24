package various

interface Base {
    fun method(a: Int)
}

class BaseDelegate : Base {
    override fun method(a: Int) {
        println("From various.BaseDelegate: $a")
    }
}

class Derived(val a: Int = 10) : Base by BaseDelegate()

fun main() {
    val derived = Derived()
    derived.method(derived.a)
}
