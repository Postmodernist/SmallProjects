package interfaces

import Constraints
import Model

interface Merger {

    fun mergeConstraints(constraints: Constraints, idA: Int, idB: Int)

    fun mergeModel(model: Model, idA: Int, idB: Int)
}