package coroutines

import kotlin.concurrent.thread

private class Deadlock {

    fun outer() {
        synchronized(this) {
            inner(Thread.currentThread().name)
            thread { inner(Thread.currentThread().name) }.join()
        }
    }

    fun inner(s: String) {
        synchronized(this) {
            println(s)
        }
    }
}

fun main() {
    Deadlock().outer()
}