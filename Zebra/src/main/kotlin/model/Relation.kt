package model

class Relation(
    val f: (Int) -> Set<Int>, // relation function
    val g: (Int) -> Set<Int>  // reciprocal of f
)