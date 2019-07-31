package core

import Constraints
import Model
import interfaces.HoradricCube
import interfaces.Matcher
import interfaces.Merger
import interfaces.Simplifier
import results.HoradricResult
import results.HoradricResult.Contradiction
import results.HoradricResult.Match

class SimplifierImpl : Simplifier {

    private lateinit var horadricCube: HoradricCube
    private lateinit var matcher: Matcher
    private lateinit var merger: Merger

    fun inject(horadricCube: HoradricCube, matcher: Matcher, merger: Merger) {
        this.horadricCube = horadricCube
        this.matcher = matcher
        this.merger = merger
    }

    override fun simplify(constraints: Constraints, model: Model): HoradricResult {
        try {
            mergeMatches(constraints, model)
        } catch (e: IllegalStateException) {
            return Contradiction
        }
        var result: HoradricResult
        do {
            result = horadricCube.transmute(constraints, model)
            if (result is Match) {
                try {
                    merger.merge(constraints, model, result.idA, result.idB)
                } catch (e: IllegalStateException) {
                    return Contradiction
                }
            }
        } while (result is Match)
        return result
    }

    private fun mergeMatches(constraints: Constraints, model: Model) {
        while (true) {
            val match = matcher.findMatch(constraints) ?: break
            merger.merge(constraints, model, match.first, match.second)
        }
    }
}
