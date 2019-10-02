package various

import kotlin.concurrent.thread

val lock = Object()

fun main() {
    val t1 = thread {
        synchronized(lock) {
            println("${Thread.currentThread().id} wait")
            lock.wait()
            println("${Thread.currentThread().id} done")
        }
    }
    val t2 = thread {
        Thread.sleep(1000)
        synchronized(lock) {
            println("${Thread.currentThread().id} notifyAll")
            lock.notifyAll()
            println("${Thread.currentThread().id} done")
        }
    }

    t1.join()
    t2.join()
}
