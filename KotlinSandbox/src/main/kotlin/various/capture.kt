package various

class Foo {
    var bar = 42

    fun capture(): () -> Unit {
        var baz = 666
        val lambda = { println() }
        return lambda
    }
}
