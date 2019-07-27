package core

import Constraints
import Model
import interfaces.HoradricCube
import interfaces.Merger
import interfaces.Simplifier
import results.HoradricResult

class SimplifierImpl(
    private val cube: HoradricCube,
    private val merger: Merger
) : Simplifier {

    override fun simplify(constraints: Constraints, model: Model): HoradricResult {
        var result: HoradricResult
        do {
            result = cube.transmute(constraints, model)
            if (result is HoradricResult.Match) {
                merger.mergeConstraints(constraints, result.idA, result.idB)
                merger.mergeModel(model, result.idA, result.idB)
            }
        } while (result is HoradricResult.Match)
        return result
    }
}
