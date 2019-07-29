package core

import Constraints
import Model
import interfaces.Contradictor
import interfaces.HoradricCube
import interfaces.Merger
import interfaces.Simplifier
import model.Constraint
import model.Entry.RuleSet
import results.HoradricResult
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class SimplifierImpl : Simplifier {

    private lateinit var horadricCube: HoradricCube
    private lateinit var merger: Merger
    private lateinit var contradictor: Contradictor

    fun inject(horadricCube: HoradricCube, merger: Merger, contradictor: Contradictor) {
        this.horadricCube = horadricCube
        this.merger = merger
        this.contradictor = contradictor
    }

    override fun simplify(constraints: Constraints, model: Model): HoradricResult {
        println("> Simplify")
        var result: HoradricResult
        while (true) {
            result = simplifyIter(constraints, model)
            if (constraints.size > Constraint.ENTRY_VARIANTS) {
                val fit = findFit(constraints, model) ?: break
                merger.mergeConstraints(constraints, fit.first, fit.second)
                merger.mergeModel(model, fit.first, fit.second)
            } else break
        }
        return result
    }

    private fun simplifyIter(constraints: Constraints, model: Model): HoradricResult {
        var result: HoradricResult
        do {
            result = horadricCube.transmute(constraints, model)
            if (result is HoradricResult.Match) {
                merger.mergeConstraints(constraints, result.idA, result.idB)
                merger.mergeModel(model, result.idA, result.idB)
            }
        } while (result is HoradricResult.Match)
        return result
    }

    private fun findFit(constraints: Constraints, model: Model): Pair<Int, Int>? {
        val fitsMap = TreeMap<Int, ArrayList<Int>>()
        for ((id, _) in model) {
            fitsMap[id] = findFitsFor(constraints, model, id)
        }
        for ((id, fits) in fitsMap) {
            val idsToRemove = ArrayList<Int>()
            for (fit in fits) {
                if (id !in fitsMap[fit]!!) {
                    idsToRemove.add(fit)
                }
            }
            fits.removeAll(idsToRemove)
            println("Fits for $id are $fits")
        }
        for ((id, fits) in fitsMap) {
            if (fits.size == 1) {
                return Pair(id, fits[0])
            }
        }
        return null
    }

    private fun findFitsFor(constraints: Constraints, model: Model, idA: Int): ArrayList<Int> {
        val constraintA = model[idA]!!
        val fits = ArrayList<Int>()
        loop@ for ((idB, constraintB) in model) {
            if (idA == idB) continue
            for (i in 0 until Constraint.ENTRIES_SIZE) {
                val entryA = constraintA[i]
                val entryB = constraintB[i]
                if (entryA.intersect(entryB).isEmpty()) {
                    continue@loop
                }
            }
            if (!distinct(constraints[idA]!!, constraints[idB]!!)) {
                fits.add(idB)
            }
        }
        return fits
    }

    private fun distinct(a: Constraint, b: Constraint): Boolean {
        return a.refersTo(b) || b.refersTo(a)
    }

    private fun Constraint.refersTo(other: Constraint): Boolean {
        val refs = HashSet<Int>()
        for (entry in entries) {
            if (entry is RuleSet) {
                for (rule in entry.rules) {
                    refs.add(rule.id)
                }
            }
        }
        return other.id in refs
    }
}
