import arrow.core.Option
import arrow.core.fix
import arrow.core.none
import arrow.instances.option.applicative.applicative
import arrow.instances.option.monad.monad
import arrow.typeclasses.binding
import kotlin.random.Random

private fun sequentialBinding() {
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

data class UUID(val uuid: Int) {
    companion object {
        fun randomUUID() = UUID(Random.nextInt())
    }
}

data class Person(val id: UUID, val name: String, val year: Int)

private fun applicativeBuilder() {
    // Note each Option is if a different type
    val maybeId = Option(UUID.randomUUID())
    val maybeName = none<String>()
    val maybeYear = Option(1926)

    val a = Option.applicative().map(maybeId, maybeName, maybeYear) { (id, name, year) ->
        Person(id, name, year)
    }.fix()

    println(a)
}

fun main(args: Array<String>) {
    sequentialBinding()
    applicativeBuilder()
}
