package interfaces

import Constraints
import Model

interface Reducer {

    fun reduce(constraints: Constraints, model: Model): Constraints?
}
