import arrow.effects.IO
import arrow.effects.async
import arrow.effects.fix
import arrow.effects.monad
import arrow.typeclasses.binding
import kotlinx.coroutines.experimental.Dispatchers

fun main(args: Array<String>) {
    val res = IO.monad().binding {
        val a = IO.invoke { 1 }.bind()
        a + 1
    }.fix().unsafeRunSync()
    println(res)

    IO.async().run {
        binding {
            // In current thread
            val id = just(1).bind()
            continueOn(Dispatchers.IO)

            // In IO dispatcher now!
            val result = IO.invoke { id * 10 }.bind()
            continueOn(Dispatchers.Default)

            // In default dispatcher now!
            println(result)
        }
    }.fix().unsafeRunSync()
}
