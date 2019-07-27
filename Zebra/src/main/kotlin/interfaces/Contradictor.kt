package interfaces

import Constraints
import Model

interface Contradictor {

    fun checkContradictions(constraints: Constraints, model: Model): Boolean
}
