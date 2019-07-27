package interfaces

import Constraints
import Model
import results.HoradricResult

interface Relaxer {

    fun relax(constraints: Constraints, model: Model): HoradricResult
}
