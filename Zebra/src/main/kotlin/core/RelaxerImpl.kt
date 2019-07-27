package core

import Constraints
import Model
import interfaces.Matcher
import interfaces.Relaxer
import model.Constraint
import model.Entry.RuleSet
import model.Entry.Value
import results.HoradricResult
import java.util.*

class RelaxerImpl : Relaxer {

    private lateinit var matcher: Matcher

    fun inject(matcher: Matcher) {
        this.matcher = matcher
    }

    override fun relax(constraints: Constraints, model: Model): HoradricResult {
        var modified = false
        var result: HoradricResult
        do {
            result = relaxIter(constraints, model)
            if (result == HoradricResult.Modified) {
                modified = true
            }
        } while (result == HoradricResult.Modified)
        return when {
            result == HoradricResult.Unchanged && !modified -> HoradricResult.Unchanged
            result == HoradricResult.Unchanged && modified -> HoradricResult.Modified
            else -> result
        }
    }

    private fun relaxIter(constraints: Constraints, model: Model): HoradricResult {
        var modified = false
        for ((id, constraint) in constraints) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry is RuleSet) {
                    val oldValues = model[id]!![i]
                    val newValues = resolveRules(model, entry, i)
                    newValues.removeClashes(constraints, model, id, i)
                    if (newValues.isEmpty()) {
                        return HoradricResult.Contradiction
                    }
                    if (newValues != oldValues) {
                        model[id]!![i] = newValues
                        if (newValues.size == 1) {
                            val v = newValues.first()
                            constraint.entries[i] = Value(v)
                            val otherId = matcher.findMatch(constraints, id, i, v)
                            if (otherId != null) {
                                return HoradricResult.Match(id, otherId)
                            }
                        }
                        modified = true
                    }
                }
            }
        }
        return if (modified) HoradricResult.Modified else HoradricResult.Unchanged
    }

    private fun resolveRules(model: Model, entry: RuleSet, i: Int): HashSet<Int> {
        val entryValues = HashSet(Constraint.defaultVariants)
        for (rule in entry.rules) {
            val ruleValues = HashSet<Int>()
            val refValues = model[rule.id]!![i]
            for (v in refValues) {
                ruleValues.addAll(rule.relation.f(v))
            }
            entryValues.retainAll(ruleValues)
        }
        return entryValues
    }

    private fun HashSet<Int>.removeClashes(
        constraints: Constraints,
        model: Model,
        constraintId: Int,
        entryIndex: Int
    ) {
        val valuesToRemove = ArrayList<Int>()
        loop@ for (v in this) {
            val idB = matcher.findMatch(constraints, constraintId, entryIndex, v) ?: continue
            for (i in 0 until Constraint.ENTRIES_SIZE) {
                if (i == entryIndex) continue
                val valuesA = model[constraintId]!![i]
                val valuesB = model[idB]!![i]
                if (valuesA.intersect(valuesB).isEmpty()) {
                    valuesToRemove.add(v)
                    continue@loop
                }
            }
        }
        removeAll(valuesToRemove)
    }
}