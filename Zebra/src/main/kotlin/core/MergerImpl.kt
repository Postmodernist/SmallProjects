package core

import Constraints
import Model
import interfaces.Merger
import model.Constraint
import model.Entry.*

class MergerImpl : Merger {

    /**
     * @throws IllegalStateException if conflict is found during merge
     */
    override fun merge(constraints: Constraints, model: Model, idA: Int, idB: Int) {
        mergeConstraints(constraints, idA, idB)
        mergeModel(model, idA, idB)
        updateValues(constraints, model)
    }

    fun mergeConstraints(constraints: Constraints, idA: Int, idB: Int) {
        val a = constraints[idA]!!
        val b = constraints[idB]!!
        for (k in 0 until Constraint.ENTRIES_SIZE) {
            a.entries[k] = when (val entry = a.entries[k]) {
                is None -> b.entries[k]
                is Value -> entry
                is RuleSet -> when (val otherEntry = b.entries[k]) {
                    is None -> entry
                    is Value -> otherEntry
                    is RuleSet -> RuleSet(entry.rules.union(otherEntry.rules))
                }
            }
        }
        constraints.remove(idB)
        constraints.updateRules(idB, idA)
    }

    fun mergeModel(model: Model, idA: Int, idB: Int) {
        val a = model[idA]!!
        val b = model[idB]!!
        for (i in a.indices) {
            a[i].retainAll(b[i])
        }
        model.remove(idB)
    }

    private fun Constraints.updateRules(oldId: Int, newId: Int) {
        for (c in values) {
            for (e in c.entries) {
                if (e is RuleSet) e.rules.forEach { if (it.id == oldId) it.id = newId }
            }
        }
    }

    private fun updateValues(constraints: Constraints, model: Model) {
        for ((id, constraint) in model) {
            for ((i, entry) in constraint.withIndex()) {
                if (entry.isEmpty()) throw IllegalStateException("Empty entry values")
                if (entry.size == 1) {
                    val v = entry.first()
                    constraints[id]!!.entries[i] = Value(v)
                }
            }
        }
    }
}
