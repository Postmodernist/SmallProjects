package core

import kotlin.math.round

@Suppress("UNCHECKED_CAST")
fun <D : Number> D.zero(): D = when (this) {
    is Byte -> 0 as D
    is Short -> 0 as D
    is Int -> 0 as D
    is Long -> 0L as D
    is Float -> 0.0f as D
    is Double -> 0.0 as D
    else -> throw IllegalArgumentException("Not a number")
}

fun Double.round(n: Int): Double {
    var m = 1.0
    repeat(n) { m *= 10 }
    return round(this * m) / m
}

operator fun Number.plus(other: Number): Number = when (this) {
    is Byte -> when (other) {
        is Byte -> plus(other)
        is Short -> plus(other)
        is Int -> plus(other)
        is Long -> plus(other)
        is Float -> plus(other)
        is Double -> plus(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Short -> when (other) {
        is Byte -> plus(other)
        is Short -> plus(other)
        is Int -> plus(other)
        is Long -> plus(other)
        is Float -> plus(other)
        is Double -> plus(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Int -> when (other) {
        is Byte -> plus(other)
        is Short -> plus(other)
        is Int -> plus(other)
        is Long -> plus(other)
        is Float -> plus(other)
        is Double -> plus(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Long -> when (other) {
        is Byte -> plus(other)
        is Short -> plus(other)
        is Int -> plus(other)
        is Long -> plus(other)
        is Float -> plus(other)
        is Double -> plus(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Float -> when (other) {
        is Byte -> plus(other)
        is Short -> plus(other)
        is Int -> plus(other)
        is Long -> plus(other)
        is Float -> plus(other)
        is Double -> plus(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Double -> when (other) {
        is Byte -> plus(other)
        is Short -> plus(other)
        is Int -> plus(other)
        is Long -> plus(other)
        is Float -> plus(other)
        is Double -> plus(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    else -> throw IllegalArgumentException("Not a number")
}

operator fun Number.compareTo(other: Number): Int = when (this) {
    is Byte -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Short -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Int -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Long -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Float -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    is Double -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Not a number")
    }
    else -> throw IllegalArgumentException("Not a number")
}
