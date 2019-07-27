package results

sealed class HoradricResult {

    object Contradiction : HoradricResult() {
        override fun toString(): String = "Contradiction"
    }

    data class Match(val idA: Int, val idB: Int) : HoradricResult()

    object Modified : HoradricResult() {
        override fun toString(): String = "Modified"
    }

    object Unchanged : HoradricResult() {
        override fun toString(): String = "Unchanged"
    }
}