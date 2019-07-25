package interfaces

import Constraints

interface Matcher {

    fun findMatch(constraints: Constraints): Pair<Int, Int>?

    fun findMatch(constraints: Constraints, constraintId: Int, entryIndex: Int, value: Int): Int?
}