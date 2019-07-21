/*
The following version of the puzzle appeared in Life International in 1962:

There are five houses.
The Englishman lives in the red house.
The Spaniard owns the dog.
Coffee is drunk in the green house.
The Ukrainian drinks tea.
The green house is immediately to the right of the ivory house.
The Old Gold smoker owns snails.
Kools are smoked in the yellow house.
Milk is drunk in the middle house.
The Norwegian lives in the first house.
The man who smokes Chesterfields lives in the house next to the man with the fox.
Kools are smoked in the house next to the house where the horse is kept.
The Lucky Strike smoker drinks orange juice.
The Japanese smokes Parliaments.
The Norwegian lives next to the blue house.
Now, who drinks water? Who owns the zebra?

In the interest of clarity, it must be added that each of the five houses is
painted a different color, and their inhabitants are of different national
extractions, own different pets, drink different beverages and smoke different
brands of American cigarets [sic]. One other thing: in statement 6, right means
your right.
*/

package various

import various.Cigarettes.*
import various.Colors.*
import various.Drinks.*
import various.Entry.*
import various.Nations.*
import various.Pets.*
import various.Relations.imRight
import various.Relations.nextTo
import java.util.*
import kotlin.collections.HashMap

class Simplifier {

    val constraints = HashMap<Int, Constraint>()
    private val matcher = MatcherImpl()
    private val evaluator = EvaluatorImpl()

    fun add(a: Constraint) {
        constraints[a.id] = a
    }

    fun simplify(): Simplifier {
        constraints.addReciprocalRelations()
        var i = 1
        var modified = true
        while (modified) {
            println(":: Iteration ${i++}\n")
            modified = false
            if (constraints.mergeMatches()) {
                modified = true
            }
            if (constraints.resolveRules()) {
                modified = true
            }
        }
        return this
    }

    private fun HashMap<Int, Constraint>.addReciprocalRelations() {
        for (constraint in values) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry is RuleSet) {
                    for (rule in entry.rules) {
                        val reciprocalRelation = Relation(rule.relation.g, rule.relation.f)
                        val reciprocalRule = Rule(reciprocalRelation, constraint.id)
                        val otherConstraint = get(rule.id)
                                ?: throw IllegalStateException("Constraint ${rule.id} not found")
                        otherConstraint.entries[i] = when (val otherEntry = otherConstraint.entries[i]) {
                            is None -> RuleSet(hashSetOf(reciprocalRule))
                            is Value -> otherEntry
                            is RuleSet -> RuleSet(otherEntry.rules + reciprocalRule)
                        }
                    }
                }
            }
        }
    }

    private fun HashMap<Int, Constraint>.mergeMatches(): Boolean {
        println("> Merge matches")
        var modified = false
        while (true) {
            val match = matcher.findMatch(this) ?: break
            merge(match.first, match.second)
            modified = true
        }
        return modified
    }

    private fun HashMap<Int, Constraint>.merge(idA: Int, idB: Int) {
        val a = get(idA)!!
        val b = get(idB)!!
        print("${a.show()} + ${b.show()} = ")
        for (k in a.entries.indices) {
            a.entries[k] = when (val entry = a.entries[k]) {
                is None -> b.entries[k]
                is Value -> entry
                is RuleSet -> when (val otherEntry = b.entries[k]) {
                    is None -> entry
                    is Value -> otherEntry
                    is RuleSet -> RuleSet(entry.rules.union(otherEntry.rules))
                }
            }
        }
        println(a.show())
        remove(idB)
        updateRules(idB, idA)
    }

    private fun HashMap<Int, Constraint>.updateRules(oldId: Int, newId: Int) {
        for (c in values) {
            for (e in c.entries) {
                if (e is RuleSet) e.rules.forEach { if (it.id == oldId) it.id = newId }
            }
        }
    }

    private fun HashMap<Int, Constraint>.resolveRules(): Boolean {
        println("> Resolve rules")
        evaluator.use(constraints)
        var modified = false
        val ids = keys.toIntArray()
        for (id in ids) {
            for (i in 0 until Constraint.ENTRIES_SIZE) {
                if (resolveEntry(id, i)) {
                    modified = true
                }
            }
        }
        println()
        return modified
    }

    private fun HashMap<Int, Constraint>.resolveEntry(constraintId: Int, entryIndex: Int): Boolean {
        val c = get(constraintId)!!
        if (c.entries[entryIndex] !is RuleSet) return false
        println("Resolve entry $entryIndex of ${c.show()}")
        val values = evaluator.possibleValues(constraintId, entryIndex)
        println("Possible values = ${Arrays.toString(values.toIntArray().apply { sort() })}")
        return if (values.size != 1) false else {
            val v = values.first()
            c.entries[entryIndex] = Value(v)
            true
        }
    }
}

class MatcherImpl : Matcher {

    override fun findMatch(constraints: Map<Int, Constraint>): Pair<Int, Int>? {
        val ids = constraints.keys.toIntArray()
        for (i in 0 until ids.size - 1) {
            for (j in i + 1 until ids.size) {
                val idA = ids[i]
                val idB = ids[j]
                if (constraints.match(idA, idB)) {
                    println("Found match: ${constraints[idA]?.show()} and ${constraints[idB]?.show()}")
                    return Pair(idA, idB)
                }
            }
        }
        return null
    }

    private fun Map<Int, Constraint>.match(idA: Int, idB: Int): Boolean {
        val a = get(idA)!!
        val b = get(idB)!!
        for (k in a.entries.indices) {
            val entryA = a.entries[k]
            val entryB = b.entries[k]
            if (entryA is Value && entryB is Value && entryA.v == entryB.v) return true
        }
        return false
    }
}

class EvaluatorImpl : Evaluator {

    private lateinit var constraints: Map<Int, Constraint>
    private lateinit var estimated: Estimated
    private lateinit var explored: Explored

    override fun use(constraints: Map<Int, Constraint>) {
        this.constraints = constraints
        estimated = Estimated(constraints)
        explored = Explored(constraints)
    }

    override fun possibleValues(constraintId: Int, entryIndex: Int): Set<Int> {
        explored.reset()
        return findPossibleValues(constraintId, entryIndex)
    }

    private fun findPossibleValues(constraintId: Int, entryIndex: Int): Set<Int> {
        val entry = constraint.entries[entryIndex] as? RuleSet
                ?: throw java.lang.IllegalArgumentException("Entry is not a RuleSet")
        var modified = true
        while (modified) {
            modified = false
            if (entry.rules.findValuesFixedPoint(constraint)) {
                modified = true
            }
            val values = estimatedValues[constraint.id]
            if (values.isNullOrEmpty()) throw IllegalStateException("No possible values found")
            val possibleValues = HashSet<Int>()
            for (v in values) {
                if (constraint.isValuePossible(v)) possibleValues.add(v)
            }
            if (possibleValues.isEmpty()) throw IllegalStateException("No possible values found")
            if (possibleValues != values) {
                estimatedValues[constraint.id] = possibleValues
                modified = true
            }
        }
        return estimatedValues.safeGet(constraint.id)
    }

    private fun Set<Rule>.findValuesFixedPoint(parent: Constraint): Boolean {
        var lastEstimatedValues: HashMap<Int, HashSet<Int>>
        var modified = false
        while (true) {
            lastEstimatedValues = HashMap(estimatedValues)
            estimateValues(parent, entryIndex, constraints, estimatedValues)
            if (estimatedValues != lastEstimatedValues) {
                modified = true
            } else {
                break
            }
        }
        return modified
    }

    private fun Set<Rule>.estimateValues(
            parent: Constraint,
            entryIndex: Int,
            constraints: List<Constraint>,
            estimatedValues: HashMap<Int, HashSet<Int>>
    ): Set<Int> {
        for (rule in this) {
            rule.evaluate(parent, entryIndex, constraints, estimatedValues)
        }
        return estimatedValues.safeGet(parent.id)
    }

    private fun Rule.evaluate(
            parent: Constraint,
            entryIndex: Int,
            constraints: List<Constraint>,
            estimatedValues: HashMap<Int, HashSet<Int>>
    ) {
        // Initialize set of values.
        if (!estimatedValues.contains(parent.id)) {
            estimatedValues[parent.id] = HashSet(Constraint.defaultVariants)
        }
        // Get constraint referenced by this rule.
        val ref = constraints.find { it.id == id }
                ?: throw IllegalStateException("Referenced id = $id not found")
        // Get set of values of entry at entryIndex of referenced constraint.
        val refValues = estimatedValues[ref.id]
                ?: when (val refEntry = ref.entries[entryIndex]) {
                    None -> Constraint.defaultVariants
                    is Value -> setOf(refEntry.v)
                    is RuleSet ->
                        refEntry.rules.estimateValues(ref, entryIndex, constraints, estimatedValues)
                }
        // Apply this rule's relation to each value of referenced entry and collect results.
        val ruleValues = HashSet<Int>()
        for (v in refValues) {
            ruleValues.addAll(relation.f(v))
        }
        if (ruleValues.isEmpty()) throw IllegalStateException("No possible values found")
        estimatedValues[parent.id]?.retainAll(ruleValues)
    }

    private fun HashMap<Int, HashSet<Int>>.safeGet(id: Int): Set<Int> {
        val result = get(id)
        if (result.isNullOrEmpty()) throw IllegalStateException("Evaluation error")
        return result
    }

    private fun Constraint.isValuePossible(v: Int): Boolean {
        constraints.filter { c ->
            val e = c.entries[entryIndex]
            e is Value && e.v == v
        }.forEach { other ->
            if (distinct(this, other)) return false
        }
        return true
    }

    private fun distinct(a: Constraint, b: Constraint, entryIndex: Int): Boolean {
        for (k in a.entries.indices) {
            if (k == entryIndex) continue // skip estimated entry
            val entryA = a.entries[k]
            val entryB = b.entries[k]
            when (entryA) {
                is Value -> when (entryB) {
                    is Value -> return entryA.v != entryB.v
                    is RuleSet -> {
                        // B depends on A
                        if (a.id in entryB.rules.map { it.id }) return true
                        // B can't have value of A
                        if (entryA.v !in possibleValues(b.id, k)) return true
                    }
                }
                is RuleSet -> when (entryB) {
                    is Value -> {
                        // A depends on B
                        if (b.id in entryA.rules.map { it.id }) return true
                        // A can't have value of B
                        if (entryB.v !in possibleValues(a.id, k)) return true
                    }
                    is RuleSet -> {
                        // One depends on the other
                        if (a.id in entryB.rules.map { it.id } || b.id in entryA.rules.map { it.id }) return true
                        // No common values
                        val valuesA = possibleValues(a.id, k)
                        val valuesB = possibleValues(b.id, k)
                        val commonValues = valuesA.intersect(valuesB)
                        if (commonValues.isEmpty()) return true
                    }
                }
            }
        }
        return false
    }
}

class Estimated(constraints: Map<Int, Constraint>) {

    val values: HashMap<Int, ArrayList<HashSet<Int>>> = HashMap(constraints.size)
    private val savedValues: HashMap<Int, ArrayList<HashSet<Int>>> = HashMap(constraints.size)

    init {
        for ((id, constraint) in constraints) {
            val constraintValues = ArrayList<HashSet<Int>>(Constraint.ENTRIES_SIZE)
            for (entry in constraint.entries) {
                val entryValues = if (entry is Value) {
                    hashSetOf(entry.v)
                } else {
                    HashSet(Constraint.defaultVariants)
                }
                constraintValues.add(entryValues)
            }
            values[id] = constraintValues
        }
    }

    fun saveSnapshot() {
        savedValues.clear()
        for ((id, constraintValues) in values) {
            val constraintValuesCopy = ArrayList<HashSet<Int>>(Constraint.ENTRIES_SIZE)
            for (entryValues in constraintValues) {
                constraintValuesCopy.add(HashSet(entryValues))
            }
            savedValues[id] = constraintValuesCopy
        }
    }

    fun isModified(): Boolean = values == savedValues
}

class Explored(constraints: Map<Int, Constraint>) {

    val entries: HashMap<Int, Array<Boolean>> = HashMap(constraints.size)

    init {
        val ids = constraints.keys.toIntArray()
        for (id in ids) {
            entries[id] = Array(Constraint.ENTRIES_SIZE) { false }
        }
    }

    fun reset() {
        for ((_, a) in entries) {
            for (i in a.indices) {
                a[i] = false
            }
        }
    }
}

interface Matcher {

    fun findMatch(constraints: Map<Int, Constraint>): Pair<Int, Int>?
}

interface Evaluator {

    fun use(constraints: Map<Int, Constraint>)

    fun possibleValues(constraintId: Int, entryIndex: Int): Set<Int>
}

// Model

class Constraint(val id: Int, vararg entries: Entry) {

    val entries = if (entries.size == ENTRIES_SIZE) arrayOf(*entries) else
        throw IllegalArgumentException("Wrong number of arguments")

    companion object {

        const val ENTRIES_SIZE = 6
        private const val ENTRY_VARIANTS = 5

        val defaultVariants: Set<Int> = (List(ENTRY_VARIANTS) { it }).toSet()
    }
}

sealed class Entry {
    object None : Entry()
    class Value(val v: Int) : Entry()
    class RuleSet(val rules: Set<Rule>) : Entry()
}

class Rule(val relation: Relation, var id: Int)

class Relation(
        val f: (Int) -> Set<Int>, // relation function
        val g: (Int) -> Set<Int>  // reciprocal of f
)

// Utility functions

fun <T : Enum<T>> value(v: T) = Value(v.ordinal)

fun value(v: Int) = Value(v)

fun rule(relation: Relation, id: Int): RuleSet = RuleSet(hashSetOf(Rule(relation, id)))

// =============================
// The Zebra Problem
// =============================

enum class Colors { RED, GREEN, IVORY, YELLOW, BLUE }
enum class Nations { ENGLISHMAN, SPANIARD, UKRAINIAN, JAPANESE, NORWEGIAN }
enum class Pets { DOG, SNAILS, FOX, HORSE, ZEBRA }
enum class Drinks { COFFEE, TEA, MILK, ORANGE_JUICE, WATER }
enum class Cigarettes { OLD_GOLD, KOOLS, CHESTERFIELDS, LUCKY_STRIKE, PARLIAMENTS }

fun Constraint.show(): String {
    val pos = if (entries[0] is Value) (entries[0] as Value).v.toString() else "?"
    val col = if (entries[1] is Value) Colors.values()[(entries[1] as Value).v].name else "?"
    val nat = if (entries[2] is Value) Nations.values()[(entries[2] as Value).v].name else "?"
    val pet = if (entries[3] is Value) Pets.values()[(entries[3] as Value).v].name else "?"
    val dri = if (entries[4] is Value) Drinks.values()[(entries[4] as Value).v].name else "?"
    val cig = if (entries[5] is Value) Cigarettes.values()[(entries[5] as Value).v].name else "?"
    return "[$pos, $col, $nat, $pet, $dri, $cig]"
}

object Relations {

    private val variants: Set<Int> = Constraint.defaultVariants

    val imRight = Relation(::imRightF, ::imLeftF)
    val nextTo = Relation(::nextToF, ::nextToF)

    private fun imRightF(v: Int): Set<Int> {
        return variants.intersect(setOf(v + 1))
    }

    private fun imLeftF(v: Int): Set<Int> {
        return variants.intersect(setOf(v - 1))
    }

    private fun nextToF(v: Int): Set<Int> {
        return variants.intersect(setOf(v - 1, v + 1))
    }
}

fun main() {
    val simplifier = Simplifier().apply {
        // ID, POSITION, COLOR, NATION, PET, DRINK, CIGARETTES
        add(Constraint(100, None, value(RED), value(ENGLISHMAN), None, None, None))
        add(Constraint(101, None, None, value(SPANIARD), value(DOG), None, None))
        add(Constraint(102, None, value(GREEN), None, None, value(COFFEE), None))
        add(Constraint(103, None, None, value(UKRAINIAN), None, value(TEA), None))
        add(Constraint(104, None, value(IVORY), None, None, None, None))
        add(Constraint(105, rule(imRight, 104), value(GREEN), None, None, None, None))
        add(Constraint(106, None, None, None, value(SNAILS), None, value(OLD_GOLD)))
        add(Constraint(107, None, value(YELLOW), None, None, None, value(KOOLS)))
        add(Constraint(108, value(2), None, None, None, value(MILK), None))
        add(Constraint(109, value(0), None, value(NORWEGIAN), None, None, None))
        add(Constraint(110, None, None, None, value(FOX), None, None))
        add(Constraint(111, rule(nextTo, 110), None, None, None, None, value(CHESTERFIELDS)))
        add(Constraint(112, None, None, None, value(HORSE), None, None))
        add(Constraint(113, rule(nextTo, 112), None, None, None, None, value(KOOLS)))
        add(Constraint(114, None, None, None, None, value(ORANGE_JUICE), value(LUCKY_STRIKE)))
        add(Constraint(115, None, None, value(JAPANESE), None, None, value(PARLIAMENTS)))
        add(Constraint(116, None, value(BLUE), None, None, None, None))
        add(Constraint(117, rule(nextTo, 116), None, value(NORWEGIAN), None, None, None))
        add(Constraint(118, None, None, None, value(ZEBRA), None, None))
        add(Constraint(119, None, None, None, None, value(WATER), None))
    }.simplify()
    for ((_, c) in simplifier.constraints) {
        println(c.show())
    }
}
