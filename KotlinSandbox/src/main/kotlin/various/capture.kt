package various

private class Capture {
    var baz: IntArray = intArrayOf(42)

    fun bar(): () -> Unit {
        var quux = intArrayOf(99)
        val lambda = {
            baz = intArrayOf(555)
            println("lambda baz[0] = ${baz[0]}")
            println("lambda quux[0] = ${quux[0]}")
        }
        quux = intArrayOf(666)
        return lambda
    }
}

fun main() {
    val foo = Capture()
    val qux = foo.bar()
    qux()
    println("foo baz[0] = ${foo.baz[0]}")
}
