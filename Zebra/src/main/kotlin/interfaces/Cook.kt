package interfaces

import Constraints
import Model

interface Cook {

    fun prepare(constraints: Constraints): Model
}