package interfaces

import Constraints
import Model

interface Merger {

    fun merge(constraints: Constraints, model: Model, idA: Int, idB: Int)
}