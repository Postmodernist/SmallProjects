package various

@Suppress("USELESS_IS_CHECK")
fun main() {
    val a = listOf("ab", "cd").fix()
    val b = arrayOf("ef", "gh").fix()

    when (a) {
        is StringList -> println("StringList -- OK")
        else -> println("StringList -- FAIL")
    }

    when (b) {
        is StringArray -> println("StringArray -- OK")
        else -> println("StringArray -- FAIL")
    }
}

class StringArray(val v: Array<String>)

class StringList(val v: List<String>)

fun Array<String>.fix() = StringArray(this)

fun List<String>.fix() = StringList(this)
