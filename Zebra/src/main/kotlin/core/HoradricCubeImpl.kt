package core

import Constraints
import Model
import interfaces.*
import model.Entry.Value
import results.HoradricResult
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2

class HoradricCubeImpl : HoradricCube {

    private lateinit var matcher: Matcher
    private lateinit var relaxer: Relaxer
    private lateinit var contradictor: Contradictor
    private lateinit var copier: Copier

    fun inject(matcher: Matcher, relaxer: Relaxer, contradictor: Contradictor, copier: Copier) {
        this.matcher = matcher
        this.relaxer = relaxer
        this.contradictor = contradictor
        this.copier = copier
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
                        val constraintsCopy = copier.copyConstraints(constraints)
                        val modelCopy = copier.copyModel(model)
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
}