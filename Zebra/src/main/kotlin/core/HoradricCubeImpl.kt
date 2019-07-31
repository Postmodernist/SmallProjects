package core

import Constraints
import Model
import interfaces.*
import model.Entry.Value
import results.HoradricResult
import results.HoradricResult.*
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2

class HoradricCubeImpl : HoradricCube {

    private lateinit var contradictor: Contradictor
    private lateinit var copier: Copier
    private lateinit var matcher: Matcher
    private lateinit var relaxer: Relaxer

    fun inject(contradictor: Contradictor, copier: Copier, matcher: Matcher, relaxer: Relaxer) {
        this.contradictor = contradictor
        this.copier = copier
        this.matcher = matcher
        this.relaxer = relaxer
    }

    /**
     * Relaxes model values until fixed point.
     * Keeps important invariant: no matches. Returns [Match] result as soon as match is produced.
     */
    override fun transmute(constraints: Constraints, model: Model): HoradricResult {
        var transmuteResult: HoradricResult = Unchanged
        var modified = true
        while (modified) {
            modified = false
            when (val result = relaxer.relax(constraints, model)) {
                is Contradiction -> return result
                is Match -> return result
                is Modified -> modified = true
            }
            when (val result = sieve(constraints, model)) {
                is Contradiction -> return result
                is Match -> return result
                is Modified -> modified = true
            }
            if (modified) {
                transmuteResult = Modified
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
                    if (entryValues.size < 2)
                        throw IllegalStateException("Too few entry values: ${entryValues.size}")
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
                    if (entryValues.isEmpty()) return Contradiction
                    if (entryValues.size == 1) {
                        val v = entryValues.first()
                        constraint.entries[i] = Value(v)
                        val otherId = matcher.findMatch(constraints, id, i, v)
                        if (otherId != null) {
                            return Match(id, otherId)
                        }
                    }
                }
            }
        }
        return if (modified) Modified else Unchanged
    }
}