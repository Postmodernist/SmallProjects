package model

data class Relation(
    val fName: String,
    val f: (Int) -> Set<Int>, // relation function
    val gName: String,
    val g: (Int) -> Set<Int>  // reciprocal of f
) {
    override fun toString(): String = fName
}
