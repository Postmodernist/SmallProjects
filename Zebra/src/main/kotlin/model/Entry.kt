package model

sealed class Entry {
    object None : Entry()
    class Value(val v: Int) : Entry()
    class RuleSet(val rules: Set<Rule>) : Entry()
}