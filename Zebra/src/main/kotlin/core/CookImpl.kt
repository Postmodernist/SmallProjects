package core

import Constraints
import Model
import interfaces.Cook
import interfaces.Matcher
import interfaces.Merger
import model.Constraint
import model.Entry
import model.Relation
import model.Rule
import java.util.ArrayList
import java.util.HashSet

class CookImpl(
    private val matcher: Matcher,
    private val merger: Merger
) : Cook {

    override fun prepare(constraints: Constraints): Model {
        constraints.addReciprocalRelations()
        constraints.mergeMatches()
        return constraints.cookModel()
    }

    private fun Constraints.addReciprocalRelations() {
        for (constraint in values) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry is Entry.RuleSet) {
                    for (rule in entry.rules) {
                        val reciprocalRelation = Relation(rule.relation.g, rule.relation.f)
                        val reciprocalRule = Rule(reciprocalRelation, constraint.id)
                        val otherConstraint = get(rule.id)
                            ?: throw IllegalStateException("Constraint ${rule.id} not found")
                        otherConstraint.entries[i] = when (val otherEntry = otherConstraint.entries[i]) {
                            is Entry.None -> Entry.RuleSet(hashSetOf(reciprocalRule))
                            is Entry.Value -> otherEntry
                            is Entry.RuleSet -> Entry.RuleSet(otherEntry.rules + reciprocalRule)
                        }
                    }
                }
            }
        }
    }

    private fun Constraints.mergeMatches() {
        println("> Merge matches")
        while (true) {
            val match = matcher.findMatch(this) ?: break
            merger.mergeConstraints(this, match.first, match.second)
        }
    }

    private fun Constraints.cookModel(): Model {
        val model: Model = HashMap()
        for ((id, constraint) in this) {
            val constraintValues =
                ArrayList<HashSet<Int>>(Constraint.ENTRIES_SIZE)
            for (entry in constraint.entries) {
                val entryValues = if (entry is Entry.Value) {
                    hashSetOf(entry.v)
                } else {
                    HashSet(Constraint.defaultVariants)
                }
                constraintValues.add(entryValues)
            }
            model[id] = constraintValues
        }
        return model
    }
}