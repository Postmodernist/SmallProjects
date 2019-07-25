package model

data class Rule(val relation: Relation, var id: Int) {
    override fun toString(): String = "($relation, $id)"
}