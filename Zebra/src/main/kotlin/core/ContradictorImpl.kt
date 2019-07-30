package core

import Constraints
import Model
import interfaces.Contradictor
import interfaces.Merger
import interfaces.Relaxer
import results.HoradricResult
import results.HoradricResult.*

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
            if (result is Match) {
                merger.merge(constraints, model, result.idA, result.idB)
            }
        } while (result is Match)
        return result is Contradiction
    }
}