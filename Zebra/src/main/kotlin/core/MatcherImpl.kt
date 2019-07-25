package core

import interfaces.Matcher
import Constraints
import model.Entry

class MatcherImpl : Matcher {

    override fun findMatch(constraints: Constraints): Pair<Int, Int>? {
        val ids = constraints.keys.toIntArray()
        for (i in 0 until ids.size - 1) {
            for (j in i + 1 until ids.size) {
                val idA = ids[i]
                val idB = ids[j]
                if (constraints.match(idA, idB)) {
                    return Pair(idA, idB)
                }
            }
        }
        return null
    }

    override fun findMatch(
        constraints: Constraints,
        constraintId: Int,
        entryIndex: Int,
        value: Int
    ): Int? {
        for ((id, constraint) in constraints) {
            if (id == constraintId) continue
            val entry = constraint.entries[entryIndex]
            if (entry is Entry.Value && entry.v == value) {
                return id
            }
        }
        return null
    }

    private fun Constraints.match(idA: Int, idB: Int): Boolean {
        val a = get(idA)!!
        val b = get(idB)!!
        for (k in a.entries.indices) {
            val entryA = a.entries[k]
            val entryB = b.entries[k]
            if (entryA is Entry.Value && entryB is Entry.Value && entryA.v == entryB.v) return true
        }
        return false
    }
}