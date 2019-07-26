package core

import Constraints
import Model
import interfaces.HoradricCube
import interfaces.Matcher
import interfaces.Merger
import model.Constraint
import model.Entry
import results.HoradricResult
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.first
import kotlin.collections.hashSetOf
import kotlin.collections.intersect
import kotlin.collections.isNotEmpty
import kotlin.collections.iterator
import kotlin.collections.set
import kotlin.collections.withIndex

class HoradricCubeImpl(
    private val matcher: Matcher,
    private val merger: Merger
) : HoradricCube {

    override fun transmute(constraints: Constraints, model: Model): HoradricResult {
        var transmuteResult: HoradricResult = HoradricResult.Unchanged
        var modified = true
        while (modified) {
            modified = false
            when (val result = relaxFixPoint(model, constraints)) {
                is HoradricResult.Contradiction -> return result
                is HoradricResult.Match -> return result
                is HoradricResult.Modified -> modified = true
            }
            when (val result = sieve(model, constraints)) {
                is HoradricResult.Contradiction -> return result
                is HoradricResult.Match -> return result
                is HoradricResult.Modified -> modified = true
            }
            if (modified) {
                transmuteResult = HoradricResult.Modified
            }
        }
        return transmuteResult
    }

    private fun relaxFixPoint(model: Model, constraints: Constraints): HoradricResult {
        var modified = false
        var result: HoradricResult
        do {
            result = relax(model, constraints)
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

    private fun relax(model: Model, constraints: Constraints): HoradricResult {
        var modified = false
        for ((id, constraint) in constraints) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry is Entry.RuleSet) {
                    val oldValues = model[id]!![i]
                    val newValues = resolveRules(model, entry, i)
                    newValues.removeClashes(model, constraints, id, i)
                    if (newValues.isEmpty()) {
                        return HoradricResult.Contradiction
                    }
                    if (newValues != oldValues) {
                        model[id]!![i] = newValues
                        if (newValues.size == 1) {
                            val v = newValues.first()
                            constraint.entries[i] = Entry.Value(v)
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

    private fun resolveRules(model: Model, entry: Entry.RuleSet, i: Int): HashSet<Int> {
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
        model: Model,
        constraints: Constraints,
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

    private fun sieve(model: Model, constraints: Constraints): HoradricResult {
        var modified = false
        for ((id, constraint) in constraints) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry !is Entry.Value) {
                    val entryValues = model[id]!![i]
                    val valuesToRemove = HashSet<Int>()
                    for (v in entryValues) {
                        val constraintsCopy = constraints.copyConstraints()
                        val modelCopy = model.copyModel()
                        modelCopy[id]!![i] = hashSetOf(v)
                        if (checkContradiction(modelCopy, constraintsCopy)) {
                            valuesToRemove.add(v)
                        }
                    }
                    if (valuesToRemove.isNotEmpty()) {
                        modified = true
                    }
                    entryValues.removeAll(valuesToRemove)
                    if (entryValues.isEmpty()) return HoradricResult.Contradiction
                    if (entryValues.size == 1) {
                        val v = entryValues.first()
                        constraint.entries[i] = Entry.Value(v)
                        val otherId = matcher.findMatch(constraints, id, i, v)
                        if (otherId != null) {
                            return HoradricResult.Match(id, otherId)
                        }
                    }
                }
            }
        }
        return if (modified) HoradricResult.Modified else HoradricResult.Unchanged
    }

    private fun checkContradiction(model: Model, constraints: Constraints): Boolean {
        var result: HoradricResult
        do {
            result = relaxFixPoint(model, constraints)
            if (result is HoradricResult.Match) {
                merger.mergeConstraints(constraints, result.idA, result.idB)
                merger.mergeModel(model, result.idA, result.idB)
            }
        } while (result is HoradricResult.Match)
        return result is HoradricResult.Contradiction
    }

    private fun Model.copyModel(): Model {
        val modelCopy: Model = HashMap()
        for ((id, constraintValues) in this) {
            val constraintValuesCopy =
                ArrayList<HashSet<Int>>(Constraint.ENTRIES_SIZE)
            for (entryValues in constraintValues) {
                constraintValuesCopy.add(HashSet(entryValues))
            }
            modelCopy[id] = constraintValuesCopy
        }
        return modelCopy
    }

    private fun Constraints.copyConstraints(): Constraints {
        val constraintsCopy: Constraints = HashMap()
        for ((id, constraint) in this) {
            val entriesCopy = Array<Entry>(Constraint.ENTRIES_SIZE) { Entry.None }
            for ((i, entry) in constraint.entries.withIndex()) {
                entriesCopy[i] = when (entry) {
                    is Entry.None -> Entry.None
                    is Entry.Value -> Entry.Value(entry.v)
                    is Entry.RuleSet -> Entry.RuleSet(entry.rules)
                }
            }
            constraintsCopy[id] = Constraint(id, *entriesCopy)
        }
        return constraintsCopy
    }
}