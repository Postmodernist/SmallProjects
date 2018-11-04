import arrow.core.Option
import arrow.core.getOrElse

fun main(args: Array<String>) {
    val maybeMsg = Option("Hello Arrow!")
    println(maybeMsg.getOrElse { "" })
}
