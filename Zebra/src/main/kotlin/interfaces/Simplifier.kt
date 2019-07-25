package interfaces

import model.Constraint
import Constraints

interface Simplifier {

    val constraints: Constraints

    fun add(constraint: Constraint)

    fun simplify(): Simplifier
}