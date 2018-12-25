package wrapper

fun foo() = 7
fun fooUpgrade() = false to 42
fun fooWrapped() = fun0Wrap(Upgradable.KEY_1, ::foo, ::fooUpgrade)

fun bar() = "bar"
fun barUpgrade() = true to "barUpgrade"
fun barWrapped() = fun0Wrap(Upgradable.KEY_2, ::bar, :: barUpgrade)

fun main(args: Array<String>) {
    println(fooWrapped())
    println(fooWrapped())
    Upgradable.KEY_1.upgrade = true
    println(fooWrapped())
    println(fooWrapped())

    println(barWrapped())
    println(barWrapped())
    Upgradable.KEY_2.upgrade = true
    println(barWrapped())
    println(barWrapped())
}
