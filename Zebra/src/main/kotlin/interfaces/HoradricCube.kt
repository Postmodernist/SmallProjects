package interfaces

import Constraints
import results.HoradricResult
import Model

interface HoradricCube {

    fun transmute(constraints: Constraints, model: Model): HoradricResult
}