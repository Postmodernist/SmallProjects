package core

import Constraints
import Model
import interfaces.Copier
import model.Constraint
import model.Entry
import java.util.*

class CopierImpl : Copier {

    override fun copyConstraints(constraints: Constraints): Constraints {
        val constraintsCopy: Constraints = TreeMap()
        for ((id, constraint) in constraints) {
            val entriesCopy = Array<Entry>(Constraint.ENTRIES_SIZE) { Entry.None }
            for ((i, entry) in constraint.entries.withIndex()) {
                entriesCopy[i] = when (entry) {
                    is Entry.None -> Entry.None
                    is Entry.Value -> Entry.Value(entry.v)
                    is Entry.RuleSet -> Entry.RuleSet(entry.rules)
                }
            }
            constraintsCopy[id] = Constraint(id, *entriesCopy)
        }
        return constraintsCopy
    }

    override fun copyModel(model: Model): Model {
        val modelCopy: Model = TreeMap()
        for ((id, constraintValues) in model) {
            val constraintValuesCopy =
                ArrayList<HashSet<Int>>(Constraint.ENTRIES_SIZE)
            for (entryValues in constraintValues) {
                constraintValuesCopy.add(HashSet(entryValues))
            }
            modelCopy[id] = constraintValuesCopy
        }
        return modelCopy
    }
}
