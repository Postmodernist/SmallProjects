package core

import Constraints
import Model
import interfaces.Contradictor
import interfaces.Merger
import interfaces.Relaxer
import results.HoradricResult

class ContradictorImpl : Contradictor {

    private lateinit var merger: Merger
    private lateinit var relaxer: Relaxer

    fun inject(merger: Merger, relaxer: Relaxer) {
        this.merger = merger
        this.relaxer = relaxer
    }

    override fun checkContradictions(constraints: Constraints, model: Model): Boolean {
        var result: HoradricResult
        do {
            result = relaxer.relax(constraints, model)
            if (result is HoradricResult.Match) {
                merger.mergeConstraints(constraints, result.idA, result.idB)
                merger.mergeModel(model, result.idA, result.idB)
            }
        } while (result is HoradricResult.Match)
        return result is HoradricResult.Contradiction
    }
}