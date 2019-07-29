package interfaces

import Constraints
import Model

interface Copier {

    fun copyConstraints(constraints: Constraints): Constraints

    fun copyModel(model: Model): Model
}