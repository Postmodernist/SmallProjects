package interfaces

import Constraints
import Model

interface Fitter {

    fun fits(constraints: Constraints, model: Model): List<Pair<Int, Int>>
}