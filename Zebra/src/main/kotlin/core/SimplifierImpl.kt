package core

import results.HoradricResult
import interfaces.Cook
import interfaces.HoradricCube
import interfaces.Merger
import interfaces.Simplifier
import model.Constraint
import Constraints

class SimplifierImpl(
    private val cook: Cook,
    private val cube: HoradricCube,
    private val merger: Merger
) : Simplifier {

    override val constraints: Constraints = HashMap()

    override fun add(constraint: Constraint) {
        constraints[constraint.id] = constraint
    }

    override fun simplify(): Simplifier {
        val model = cook.prepare(constraints)
        loop@ while (true) {
            when (val result = cube.transmute(constraints, model)) {
                is HoradricResult.Unchanged, HoradricResult.Modified ->
                    break@loop
                is HoradricResult.Contradiction ->
                    throw IllegalStateException("Contradiction")
                is HoradricResult.Match -> {
                    merger.mergeConstraints(constraints, result.idA, result.idB)
                    merger.mergeModel(model, result.idA, result.idB)
                }
            }
        }
        return this
    }
}
