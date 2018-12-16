import arrow.core.*
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.instances.either.applicative.applicative
import arrow.instances.either.monad.monad
import arrow.typeclasses.binding

private fun eitherBasics() {
    var result: Either<KnownError, Int>

    // Construct values
    result = Right(1)
    println(result)  // Right(1)

    result = Left(KnownError)
    println(result)  // Left(KnownError)

    // Lift values
    result = 1.right()
    println(result)  // Right(1)

    result = KnownError.left()
    println(result)  // Left(KnownError)

    // Extract inner values
    result = Right(1)
    var a = when (result) {
        is Left -> 0
        is Right -> result.b
    }
    println(a)  // 1

    // Contemplate both cases
    result = Right(1)
    a = result.fold({ 0 }) { it }
    println(a)  // 1

    // Provide default value
    result = Left(KnownError)
    a = result.getOrElse { 0 }
    println(a)  // 0

    // Transform B into C in Either<A, B>
    result = Right(1)
    val b = result.map { it + 1 }
    println(b)  // Right(2)

    val res: Either<KnownError, Int> = Left(KnownError)
    val c = res.map { it + 1 }
    println(c)  // Left(KnownError)

    // Compute over the contents of multiple Either<A, B> values
    val result1: Either<KnownError, Int> = Right(1)
    val result2: Either<KnownError, Int> = Right(2)
    val d = result1.flatMap { one ->
        result2.map { two ->
            one + two
        }
    }
    println(d)  // Right(3)
}

private fun monadBinding() {
    val result1: Either<KnownError, Int> = Right(1)
    val result2: Either<KnownError, Int> = Right(2)

    val a = Either.monad<KnownError>().binding {
        val one = result1.bind()
        val two = result2.bind()
        one + two
    }.fix()

    println(a)  // Right(3)
}

private fun applicativeBuilder() {
    // Note each Either is of a different type
    val eId = Right(UUID.randomUUID())
    val eName = Right("William Alvin Howard")
    val eYear = Right(1926)

    val a = Either.applicative<KnownError>().map(eId, eName, eYear) {(id, name, year) ->
        Person(id, name, year)
    }.fix()
    println(a) // Right(Person(<uuid>, "William Alvin Howard", 1926))
}

@Suppress("UNREACHABLE_CODE", "UNUSED_DESTRUCTURED_PARAMETER_ENTRY")
private fun applicativeBuilderShortCircuit() {
    // Note each Either is of a different type
    val eId = Right(UUID.randomUUID())
    val eName = Left(KnownError)
    val eYear = Right(1926)

    val a = Either.applicative<KnownError>().map(eId, eName, eYear) {(id, name, year) ->
        Person(id, name, year)
    }.fix()
    println(a) // Left(KnownError)
}

fun main(args: Array<String>) {
    eitherBasics()
    monadBinding()
    applicativeBuilder()
    applicativeBuilderShortCircuit()
}