package results

sealed class HoradricResult {

    object Contradiction : HoradricResult()

    class Match(val idA: Int, val idB: Int) : HoradricResult()

    object Modified : HoradricResult()

    object Unchanged : HoradricResult()
}