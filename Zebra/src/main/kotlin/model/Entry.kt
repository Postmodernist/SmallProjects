package model

sealed class Entry {

    object None : Entry() {
        override fun toString(): String = "None"
    }

    data class Value(val v: Int) : Entry() {
        override fun toString(): String = "$v"
    }

    data class RuleSet(val rules: Set<Rule>) : Entry() {
        override fun toString(): String = "$rules"
    }
}