package interfaces

import Constraints
import Model
import results.HoradricResult

interface Simplifier {

    fun simplify(constraints: Constraints, model: Model): HoradricResult
}
