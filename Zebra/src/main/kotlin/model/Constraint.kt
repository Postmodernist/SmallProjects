package model

class Constraint(val id: Int, vararg entries: Entry) {

    val entries = if (entries.size == ENTRIES_SIZE) arrayOf(*entries) else
        throw IllegalArgumentException("Wrong number of arguments")

    companion object {

        const val ENTRIES_SIZE = 6
        private const val ENTRY_VARIANTS = 5

        val defaultVariants: Set<Int> = (List(ENTRY_VARIANTS) { it }).toSet()
    }
}