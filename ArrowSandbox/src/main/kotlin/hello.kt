import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.monad
import arrow.typeclasses.binding

fun main(args: Array<String>) {
    val a = IO.monad().binding {
        1
    }.fix().unsafeRunSync()
    println(a)
}