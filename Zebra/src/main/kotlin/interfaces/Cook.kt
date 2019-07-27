package interfaces

import Constraints
import Model
import model.Constraint

interface Cook {

    fun add(constraint: Constraint)

    fun prepare(): Pair<Constraints, Model>
}
