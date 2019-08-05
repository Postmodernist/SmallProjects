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
    else -> throw IllegalArgumentException("Unsupported number type")
}

fun Double.round(n: Int): Double {
    var m = 1.0
    repeat(n) { m *= 10 }
    return round(this * m) / m
}

@Suppress("UNCHECKED_CAST")
operator fun <D : Number> D.plus(other: D): D = when (this) {
    is Byte -> when (other) {
        is Byte -> plus(other).toByte() as D
        else -> throw IllegalArgumentException("Operands types mismatch")
    }
    is Short -> when (other) {
        is Short -> plus(other).toShort() as D
        else -> throw IllegalArgumentException("Operands types mismatch")
    }
    is Int -> when (other) {
        is Int -> plus(other) as D
        else -> throw IllegalArgumentException("Operands types mismatch")
    }
    is Long -> when (other) {
        is Long -> plus(other) as D
        else -> throw IllegalArgumentException("Operands types mismatch")
    }
    is Float -> when (other) {
        is Float -> plus(other) as D
        else -> throw IllegalArgumentException("Operands types mismatch")
    }
    is Double -> when (other) {
        is Double -> plus(other) as D
        else -> throw IllegalArgumentException("Operands types mismatch")
    }
    else -> throw IllegalArgumentException("Unsupported number type")
}

operator fun Number.compareTo(other: Number): Int = when (this) {
    is Byte -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Unsupported number type")
    }
    is Short -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Unsupported number type")
    }
    is Int -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Unsupported number type")
    }
    is Long -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Unsupported number type")
    }
    is Float -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Unsupported number type")
    }
    is Double -> when (other) {
        is Byte -> compareTo(other)
        is Short -> compareTo(other)
        is Int -> compareTo(other)
        is Long -> compareTo(other)
        is Float -> compareTo(other)
        is Double -> compareTo(other)
        else -> throw IllegalArgumentException("Unsupported number type")
    }
    else -> throw IllegalArgumentException("Unsupported number type")
}
