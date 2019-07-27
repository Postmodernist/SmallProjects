package core

import Constraints
import Model
import interfaces.Contradictor
import interfaces.HoradricCube
import interfaces.Matcher
import interfaces.Relaxer
import model.Constraint
import model.Entry
import model.Entry.*
import results.HoradricResult
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class HoradricCubeImpl : HoradricCube {

    private lateinit var matcher: Matcher
    private lateinit var relaxer: Relaxer
    private lateinit var contradictor: Contradictor

    fun inject(matcher: Matcher, relaxer: Relaxer, contradictor: Contradictor) {
        this.matcher = matcher
        this.relaxer = relaxer
        this.contradictor = contradictor
    }

    override fun transmute(constraints: Constraints, model: Model): HoradricResult {
        var transmuteResult: HoradricResult = HoradricResult.Unchanged
        var modified = true
        while (modified) {
            modified = false
            when (val result = relaxer.relax(constraints, model)) {
                is HoradricResult.Contradiction -> return result
                is HoradricResult.Match -> return result
                is HoradricResult.Modified -> modified = true
            }
            when (val result = sieve(constraints, model)) {
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

    private fun sieve(constraints: Constraints, model: Model): HoradricResult {
        var modified = false
        for ((id, constraint) in constraints) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry !is Value) {
                    val entryValues = model[id]!![i]
                    if (entryValues.size < 2) throw IllegalStateException("Too few entry values")
                    val valuesToRemove = HashSet<Int>()
                    for (v in entryValues) {
                        val constraintsCopy = constraints.copyConstraints()
                        val modelCopy = model.copyModel()
                        constraintsCopy[id]!!.entries[i] = Value(v)
                        modelCopy[id]!![i] = hashSetOf(v)
                        if (contradictor.checkContradictions(constraintsCopy, modelCopy)) {
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
                        constraint.entries[i] = Value(v)
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

    private fun Model.copyModel(): Model {
        val modelCopy: Model = TreeMap()
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
        val constraintsCopy: Constraints = TreeMap()
        for ((id, constraint) in this) {
            val entriesCopy = Array<Entry>(Constraint.ENTRIES_SIZE) { None }
            for ((i, entry) in constraint.entries.withIndex()) {
                entriesCopy[i] = when (entry) {
                    is None -> None
                    is Value -> Value(entry.v)
                    is RuleSet -> RuleSet(entry.rules)
                }
            }
            constraintsCopy[id] = Constraint(id, *entriesCopy)
        }
        return constraintsCopy
    }
}