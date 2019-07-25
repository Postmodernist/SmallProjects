package model

import java.util.*

class Constraint(val id: Int, vararg entries: Entry) {

    val entries = if (entries.size == ENTRIES_SIZE) arrayOf(*entries) else
        throw IllegalArgumentException("Wrong number of arguments")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Constraint

        if (id != other.id) return false
        if (!entries.contentEquals(other.entries)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + entries.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "[$id, ${Arrays.toString(entries)}]"
    }

    companion object {

        const val ENTRIES_SIZE = 6
        private const val ENTRY_VARIANTS = 5

        val defaultVariants: Set<Int> = (List(ENTRY_VARIANTS) { it }).toSet()
    }
}