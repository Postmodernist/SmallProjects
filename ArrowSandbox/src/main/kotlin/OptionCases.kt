import arrow.core.*
import arrow.instances.option.applicative.applicative
import arrow.instances.option.monad.monad
import arrow.typeclasses.binding
import kotlin.random.Random

private fun optionBasics() {
    // Construct values
    var maybeInt = Option(1)  // Some(1)
    println(maybeInt)

    // Represent absent values
    maybeInt = None  // None
    println(maybeInt)

    // Convert nullable to Option
    var a: Int? = null
    maybeInt = Option.fromNullable(a)  // None
    println(maybeInt)

    // Convert Option to nullable
    maybeInt = Option(1)
    a = maybeInt.orNull()  // 1
    println(a)

    // Construct values with extensions
    maybeInt = 1.some()  // Some(1)
    println(maybeInt)
    val maybeInt2 = none<Int>()  // None
    println(maybeInt2)

    // Extract inner values
    maybeInt = Option(1)
    a = when (maybeInt) {
        is None -> 0
        is Some -> maybeInt.t
    }  // 1
    println(a)

    // Contemplate both values
    maybeInt = Option(1)
    a = maybeInt.fold({ 0 }, { it })  // 1
    println(a)

    // Provide the default if None
    maybeInt = None
    a = maybeInt.getOrElse { 0 }  // 0
    println(a)

    // Transform A into B in Option<A>
    maybeInt = Option(1)
    maybeInt = maybeInt.map { it + 1 }  // Some(2)
    println(maybeInt)
    maybeInt = none()
    maybeInt = maybeInt.map { it + 1 }  // None
    println(maybeInt)

    // Compute over the contents of multiple Option<*> values
    val maybeOne = Option(1)
    val maybeTwo = Option(2)
    maybeInt = maybeOne.flatMap { one ->
        maybeTwo.map { two ->
            one + two
        }
    }  // Some(3)
    println(maybeInt)
}

private fun sequentialBinding() {
    val maybeOne = Option(1)
    val maybeTwo = Option(2)
    val maybeThree = Option(3)

    val a = Option.monad().binding {
        val one = maybeOne.bind()
        val two = maybeTwo.bind()
        val three = maybeThree.bind()
        one + two + three
    }.fix()  // Some(6)

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
    val maybeName = Option("William Alvin Howard")
    val maybeYear = Option(1926)

    val a = Option.applicative().map(maybeId, maybeName, maybeYear) { (id, name, year) ->
        Person(id, name, year)
    }.fix()  // Some(Person(id=<uuid>, name=William Alvin Howard, year=1926))

    println(a)
}

fun main(args: Array<String>) {
    optionBasics()
    sequentialBinding()
    applicativeBuilder()
}
