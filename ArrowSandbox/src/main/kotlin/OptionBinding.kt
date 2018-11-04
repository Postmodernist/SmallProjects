import arrow.core.Option
import arrow.core.fix
import arrow.instances.option.monad.monad
import arrow.typeclasses.binding

fun main(args: Array<String>) {
    val maybeOne = Option(1)
    val maybeTwo = Option(2)
    val maybeThree = Option(3)

    val a = Option.monad().binding {
        val one = maybeOne.bind()
        val two = maybeTwo.bind()
        val three = maybeThree.bind()
        one + two + three
    }.fix()

    println(a)
}
