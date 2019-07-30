package core

import Constraints
import Model
import interfaces.Fitter
import model.Constraint
import model.Entry

class FitterImpl : Fitter {

    override fun fits(constraints: Constraints, model: Model): List<Pair<Int, Int>> {
        val ids = model.keys.toIntArray()
        val result = ArrayList<Pair<Int, Int>>()
        for (i in 0 until constraints.size - 1) {
            val idA = ids[i]
            loop@ for (j in i + 1 until constraints.size) {
                val idB = ids[j]
                for (k in 0 until Constraint.ENTRIES_SIZE) {
                    val entryA = model[idA]!![k]
                    val entryB = model[idB]!![k]
                    if (entryA.intersect(entryB).isEmpty()) {
                        continue@loop
                    }
                }
                val a = constraints[idA]!!
                val b = constraints[idB]!!
                if (a.refersTo(b) || b.refersTo(a)) {
                    continue@loop
                }
                result.add(Pair(idA, idB))
            }
        }
        return result
    }

    private fun Constraint.refersTo(other: Constraint): Boolean {
        val refs = HashSet<Int>()
        for (entry in entries) {
            if (entry is Entry.RuleSet) {
                for (rule in entry.rules) {
                    refs.add(rule.id)
                }
            }
        }
        return other.id in refs
    }
}