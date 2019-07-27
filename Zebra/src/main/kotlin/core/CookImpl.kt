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

class CookImpl : Cook {

    private lateinit var matcher: Matcher
    private lateinit var merger: Merger

    private val constraints: Constraints = HashMap()

    fun inject(matcher: Matcher, merger: Merger) {
        this.matcher = matcher
        this.merger = merger
    }

    override fun add(constraint: Constraint) {
        constraints[constraint.id] = constraint
    }

    override fun prepare(): Pair<Constraints, Model> {
        constraints.addReciprocalRelations()
        constraints.mergeMatches()
        val model = constraints.cookModel()
        return Pair(constraints, model)
    }

    private fun Constraints.addReciprocalRelations() {
        println("> Add reciprocal relations")
        for (constraint in values) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry is Entry.RuleSet) {
                    for (rule in entry.rules) {
                        val reciprocalRelation = Relation(
                            rule.relation.gName,
                            rule.relation.g,
                            rule.relation.fName,
                            rule.relation.f
                        )
                        val reciprocalRule = Rule(reciprocalRelation, constraint.id)
                        val otherConstraint = get(rule.id)
                            ?: throw IllegalStateException("Constraint ${rule.id} not found")
                        val otherEntry = otherConstraint.entries[i]
                        otherConstraint.entries[i] = when (otherEntry) {
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
        println("> Cook model")
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