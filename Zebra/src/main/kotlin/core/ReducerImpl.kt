package core

import Constraints
import interfaces.Copier
import interfaces.Matcher
import interfaces.Merger
import interfaces.Reducer
import model.Constraint
import model.Entry.*
import results.HoradricResult
import results.HoradricResult.*
import java.util.*

class ReducerImpl : Reducer {

    private lateinit var matcher: Matcher
    private lateinit var merger: Merger
    private lateinit var copier: Copier

    fun inject(matcher: Matcher, merger: Merger, copier: Copier) {
        this.matcher = matcher
        this.merger = merger
        this.copier = copier
    }

    override fun reduce(constraints: Constraints): Constraints? {
        if (constraints.size <= Constraint.ENTRY_VARIANTS) {
            return constraints
        }
        var length = constraints.size
        val queue: Queue<Constraints> = LinkedList()
        val explored = HashSet<Constraints>()
        queue.offer(constraints)
        explored.add(constraints)
        while (queue.isNotEmpty()) {
            val item = queue.poll()
            if (item.size < length) {
                length = item.size
                println("Exploring length = $length, queue size = ${queue.size}")
            }
            for ((idA, idB) in item.fits()) {
                val itemCopy = copier.copyConstraints(item)
                merger.mergeConstraints(itemCopy, idA, idB)
                if (itemCopy.simplify()) {
                    if (itemCopy.size <= Constraint.ENTRY_VARIANTS) {
                        return itemCopy
                    } else {
                        if (itemCopy !in explored) {
                            explored.add(itemCopy)
                            queue.offer(itemCopy)
                        }
                    }
                }
            }
        }
        return null
    }

    private fun Constraints.fits(): List<Pair<Int, Int>> {
        val result = ArrayList<Pair<Int, Int>>()
        val ids = keys.toIntArray()
        for (i in 0 until size - 1) {
            for (j in i + 1 until size) {
                if (fit(ids[i], ids[j])) {
                    result.add(Pair(ids[i], ids[j]))
                }
            }
        }
        return result
    }

    private fun Constraints.fit(idA: Int, idB: Int): Boolean {
        val constraintA = this[idA]!!
        val constraintB = this[idB]!!
        for (i in 0 until Constraint.ENTRIES_SIZE) {
            val entryA = constraintA.entries[i]
            val entryB = constraintB.entries[i]
            if (entryA is Value && entryB is Value && entryA.v != entryB.v) {
                return false
            }
            if (entryA is RuleSet && idB in entryA.refs()) {
                return false
            }
            if (entryB is RuleSet && idA in entryB.refs()) {
                return false
            }
        }
        return true
    }

    private fun RuleSet.refs(): Set<Int> {
        val refs = HashSet<Int>()
        for (rule in rules) {
            refs.add(rule.id)
        }
        return refs
    }

    private fun Constraints.simplify(): Boolean {
        var result: HoradricResult
        do {
            result = resolveRules()
            if (result is Match) {
                if (fit(result.idA, result.idB)) {
                    merger.mergeConstraints(this, result.idA, result.idB)
                } else {
                    return false // found contradiction
                }
            }
        } while (result is Match)
        return result !is Contradiction
    }

    private fun Constraints.resolveRules(): HoradricResult {
        var modified = false
        for ((id, constraint) in this) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry is RuleSet) {
                    val values = HashSet(Constraint.defaultVariants)
                    for (rule in entry.rules) {
                        val refEntry = this[rule.id]!!.entries[i]
                        if (refEntry is Value) {
                            values.retainAll(rule.relation.f(refEntry.v))
                        }
                    }
                    if (values.isEmpty()) {
                        return Contradiction
                    }
                    if (values.size == 1) {
                        val v = values.first()
                        constraint.entries[i] = Value(v)
                        val otherId = matcher.findMatch(this, id, i, v)
                        if (otherId != null) {
                            return Match(id, otherId)
                        }
                        modified = true
                    }
                }
            }
        }
        return if (modified) Modified else Unchanged
    }
}
