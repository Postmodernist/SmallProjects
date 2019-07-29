package core

import Constraints
import Model
import interfaces.Merger
import model.Constraint
import model.Entry

class MergerImpl : Merger {

    override fun mergeConstraints(constraints: Constraints, idA: Int, idB: Int) {
        val a = constraints[idA]!!
        val b = constraints[idB]!!
        for (k in 0 until Constraint.ENTRIES_SIZE) {
            a.entries[k] = when (val entry = a.entries[k]) {
                is Entry.None -> b.entries[k]
                is Entry.Value -> entry
                is Entry.RuleSet -> when (val otherEntry = b.entries[k]) {
                    is Entry.None -> entry
                    is Entry.Value -> otherEntry
                    is Entry.RuleSet -> Entry.RuleSet(entry.rules.union(otherEntry.rules))
                }
            }
        }
        constraints.remove(idB)
        constraints.updateRules(idB, idA)
    }

    private fun Constraints.updateRules(oldId: Int, newId: Int) {
        for (c in values) {
            for (e in c.entries) {
                if (e is Entry.RuleSet) e.rules.forEach { if (it.id == oldId) it.id = newId }
            }
        }
    }

    override fun mergeModel(model: Model, idA: Int, idB: Int) {
        val a = model[idA]!!
        val b = model[idB]!!
        for (i in a.indices) {
            a[i].retainAll(b[i])
        }
        model.remove(idB)
    }
}
