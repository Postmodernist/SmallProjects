package various

private object ObjectInit {
    init {
        println("[${Thread.currentThread().name}] init ObjectInit")
    }

    fun f() {
        println("[${Thread.currentThread().name}] ObjectInit.f() called");
    }
}

fun main() {
    println("[${Thread.currentThread().name}] start")
    ObjectInit.f()
}