@file:Suppress("UNREACHABLE_CODE", "UNUSED_VARIABLE")

import arrow.core.*
import arrow.instances.`try`.monadError.monadError
import arrow.typeclasses.bindingCatch

private fun tryBasics() {
    var result: Try<Int>
    var x: Int

    // Try :: invoke(() -> A)
    result = Try { throw RuntimeException("BOOM!") }
    println(result) // Failure(exception=java.lang.RuntimeException: BOOM!)

    // Try :: when
    result = Try { 1 }
    x = when (result) {
        is Failure -> 0
        is Success -> result.value
    }
    println(x) // 1

    // Try :: fold
    result = Try { 1 }
    x = result.fold({ 0 }, { it })
    println(x) // 1

    // Try :: recover
    result = Try { throw RuntimeException("BOOM!") }
    println(result.recover { 0 }) // Success(value=0)

    // Try :: map
    result = Try { 1 }
    var transformed: Try<String> = result.map { "got a $it" }
    println(transformed) // Success(value=got a 1)

    result = Try { throw RuntimeException("BOOM!") }
    transformed = result.map { "got a $it" }
    println(transformed) // Failure(exception=java.lang.RuntimeException: BOOM!)

    // Try :: flatMap
    val result1: Try<Int> = Try { 1 }
    val result2: Try<Int> = Try { 2 }
    result = result1.flatMap { one ->
        result2.map { two ->
            one + two
        }
    }
    println(result) // Success(value=3)
}

private fun monadBinding() {
    var result: Try<Int>
    val result1: Try<Int> = Try { 1 }
    val result2: Try<Int> = Try { 2 }

    result = Try.monadError().bindingCatch {
        val one = result1.bind()
        val two = result2.bind()
        one + two
    }.fix()
    println(result) // Success(value=3)

    result = Try.monadError().bindingCatch {
        val one = result1.bind()
        val two = result2.bind()
        throw RuntimeException("BOOM!")
        one + two
    }.fix()
    println(result) // Failure(exception=java.lang.RuntimeException: BOOM!)
}

fun main(args: Array<String>) {
    tryBasics()
    monadBinding()
}