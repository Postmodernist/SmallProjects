package interfaces
import Constraints

interface Reducer {

    fun reduce(constraints: Constraints): Constraints?
}
