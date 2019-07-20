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

class Simplifier {

    val constraints = ArrayList<Constraint>()
    private val constraintExtensions = ConstraintsExtensions()

    fun add(a: Constraint) {
        constraints += a
    }

    fun simplify(): Simplifier {
        with(constraintExtensions) {
            constraints.sortBy { it.id }
            constraints.addReciprocalRelations()
            var i = 1
            var modified = true
            while (modified) {
                println("=== Merge cycle ${i++} ===\n")
                modified = false
                if (constraints.mergeMatches()) {
                    modified = true
                }
                if (constraints.resolveRules()) {
                    modified = true
                }
            }
        }
        return this
    }
}

class ConstraintsExtensions {

    private val evaluator = EvaluatorImpl()
    private val matcher = MatcherImpl(evaluator)

    fun ArrayList<Constraint>.addReciprocalRelations() {
        println("> Add reciprocal relations")
        for (constraint in this) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry is RuleSet) {
                    for (rule in entry.rules) {
                        val reciprocalRelation = Relation(rule.relation.g, rule.relation.f)
                        val reciprocalRule = Rule(reciprocalRelation, constraint.id)
                        val otherConstraint = find { it.id == rule.id }
                                ?: throw IllegalStateException("Id ${rule.id} not found")
                        otherConstraint.entries[i] = when (val otherEntry = otherConstraint.entries[i]) {
                            is None -> RuleSet(hashSetOf(reciprocalRule))
                            is Value -> otherEntry
                            is RuleSet -> RuleSet(otherEntry.rules + reciprocalRule)
                        }
                    }
                }
            }
        }
        println()
    }

    fun ArrayList<Constraint>.mergeMatches(): Boolean {
        println("> Merge matches")
        var modified = false
        while (true) {
            val match = matcher.findMatch(this) ?: break
            merge(match.first, match.second)
            modified = true
        }
        return modified
    }

    private fun ArrayList<Constraint>.merge(i: Int, j: Int) {
        val a = get(i)
        val b = get(j)
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
        removeAt(j)
        updateRules(b.id, a.id)
    }

    private fun ArrayList<Constraint>.updateRules(oldId: Int, newId: Int) {
        for (c in this) {
            for (e in c.entries) {
                if (e is RuleSet) e.rules.forEach { if (it.id == oldId) it.id = newId }
            }
        }
    }

    fun ArrayList<Constraint>.resolveRules(): Boolean {
        println("> Resolve rules")
        var modified = false
        loop@ while (true) {
            for (c in this) {
                for (i in c.entries.indices) {
                    if (resolveEntry(c, i)) {
                        modified = true
                        continue@loop
                    }
                }
            }
            break
        }
        println()
        return modified
    }

    private fun ArrayList<Constraint>.resolveEntry(c: Constraint, i: Int): Boolean {
        if (c.entries[i] !is RuleSet) return false
        println("Resolve entry $i of ${c.show()}")
        val values = evaluator.possibleValues(c, i, this)
        println("Possible values = ${Arrays.toString(values.toIntArray().apply { sort() })}")
        return if (values.size != 1) false else {
            val v = values.first()
            c.entries[i] = Value(v)
            true
        }
    }
}

class MatcherImpl(private val evaluator: Evaluator) : Matcher {

    override fun findMatch(constraints: List<Constraint>): Pair<Int, Int>? {
        for (i in 0 until constraints.lastIndex) {
            for (j in i + 1 until constraints.size) {
                if (constraints.match(i, j)) {
                    println("Found match: ${constraints[i].show()} and ${constraints[j].show()}")
                    return Pair(i, j)
                }
            }
        }
        return null
    }

    private fun List<Constraint>.match(i: Int, j: Int): Boolean {
        val a = get(i)
        val b = get(j)
        for (k in a.entries.indices) {
            val entryA = a.entries[k]
            val entryB = b.entries[k]
            if (entryA is Value && entryB is Value && entryA.v == entryB.v) return true
        }
        return false
    }

    fun findIndirectMatch(constraints: List<Constraint>): Pair<Int, Int>? {
        for (i in constraints.indices) {
            var foundMatch = false
            var jSaved = -1
            loop@ for (j in constraints.indices) {
                if (i == j) continue@loop
                if (constraints.indirectMatch(i, j)) {
                    println("Found indirect match: ${constraints[i].show()} and ${constraints[j].show()}")
                    if (!foundMatch) {
                        jSaved = j
                        foundMatch = true
                    } else {
                        jSaved = -1
                        break@loop
                    }
                }
            }
            if (jSaved != -1) {
                println()
                return if (i < jSaved) Pair(i, jSaved) else Pair(jSaved, i)
            }
        }
        println()
        return null
    }

    private fun List<Constraint>.indirectMatch(i: Int, j: Int): Boolean {
        val a = get(i)
        val b = get(j)
        for (k in a.entries.indices) {
            val entryA = a.entries[k]
            val entryB = b.entries[k]
            when (entryA) {
                is Value -> when (entryB) {
                    is Value -> return entryA.v == entryB.v
                    is RuleSet -> {
                        // B depends on A
                        if (a.id in entryB.rules.map { it.id }) return false
                        // B can't have value of A
                        if (entryA.v !in evaluator.possibleValues(b, k, this)) return false
                    }
                }
                is RuleSet -> when (entryB) {
                    is Value -> {
                        // A depends on B
                        if (b.id in entryA.rules.map { it.id }) return false
                        // A can't have value of B
                        if (entryB.v !in evaluator.possibleValues(a, k, this)) return false
                    }
                    is RuleSet -> {
                        // One depends on the other
                        if (a.id in entryB.rules.map { it.id } || b.id in entryA.rules.map { it.id }) return false
                        // No common values
                        val valuesA = evaluator.possibleValues(a, k, this)
                        val valuesB = evaluator.possibleValues(b, k, this)
                        val commonValues = valuesA.intersect(valuesB)
                        if (commonValues.isEmpty()) return false
                    }
                }
            }
        }
        return true
    }
}

class EvaluatorImpl : Evaluator {

    override fun possibleValues(
            parent: Constraint,
            entryIndex: Int,
            constraints: List<Constraint>
    ): Set<Int> {
        val entry = parent.entries[entryIndex] as? RuleSet
                ?: throw java.lang.IllegalArgumentException("Entry is not a RuleSet")
        val values = entry.rules.possibleValues(parent, entryIndex, constraints)
        when (values.size) {
            0 -> throw IllegalStateException("No possible values found")
            1 -> return values
        }
        val possibleValues = values.filter { v ->
            isValuePossible(v, parent, entryIndex, constraints)
        }
        if (possibleValues.isEmpty()) throw IllegalStateException("No possible values found")
        return possibleValues.toSet()
    }

    private fun Set<Rule>.possibleValues(
            parent: Constraint,
            entryIndex: Int,
            constraints: List<Constraint>,
            knownValues: HashMap<Int, HashSet<Int>> = HashMap()
    ): Set<Int> {
        // Rule references entry at the same position as itself.
        // Sweep horizontally across constraints resolving values of entries at entryIndex.
        // Keep found values in the map knownValues.
        for (rule in this) {
            rule.evaluate(parent, entryIndex, constraints, knownValues)
        }
        return knownValues[parent.id] ?: throw IllegalStateException("Rule evaluation error")
    }

    private fun Rule.evaluate(
            parent: Constraint,
            entryIndex: Int,
            constraints: List<Constraint>,
            knownValues: HashMap<Int, HashSet<Int>>
    ): Set<Int> {
        // Initialize set of values for parent.
        if (!knownValues.contains(parent.id)) {
            knownValues[parent.id] = HashSet(Constraint.defaultVariants)
        }
        // Get constraint referenced by this rule.
        val referenced = constraints.find { it.id == id }
                ?: throw IllegalStateException("Referenced id = $id not found")
        // Get set of values of entry at entryIndex of referenced constraint.
        val referencedValues = knownValues[referenced.id]
                ?: when (val entry = referenced.entries[entryIndex]) {
                    None -> Constraint.defaultVariants
                    is Value -> setOf(entry.v)
                    is RuleSet ->
                        entry.rules.possibleValues(referenced, entryIndex, constraints, knownValues)
                }
        // Apply this rule's relation to each value of referenced entry and collect results.
        val ruleValues = HashSet<Int>()
        for (v in referencedValues) {
            ruleValues.addAll(relation.f(v))
        }
        knownValues[parent.id]?.retainAll(ruleValues)
        return ruleValues
    }

    private fun isValuePossible(
            v: Int,
            parent: Constraint,
            entryIndex: Int,
            constraints: List<Constraint>
    ): Boolean {
        val c = constraints.find {
            val e = it.entries[entryIndex]
            e is Value && e.v == v
        }
        return c == null || !constraints.distinct(parent, c, entryIndex)
    }

    private fun List<Constraint>.distinct(
            a: Constraint,
            b: Constraint,
            entryIndex: Int
    ): Boolean {
        for (k in a.entries.indices) {
            if (k == entryIndex) continue
            val entryA = a.entries[k]
            val entryB = b.entries[k]
            when (entryA) {
                is Value -> when (entryB) {
                    is Value -> return entryA.v != entryB.v
                    is RuleSet -> {
                        // B depends on A
                        if (a.id in entryB.rules.map { it.id }) return true
                        // B can't have value of A
                        if (entryA.v !in possibleValues(b, k, this)) return true
                    }
                }
                is RuleSet -> when (entryB) {
                    is Value -> {
                        // A depends on B
                        if (b.id in entryA.rules.map { it.id }) return true
                        // A can't have value of B
                        if (entryB.v !in possibleValues(a, k, this)) return true
                    }
                    is RuleSet -> {
                        // One depends on the other
                        if (a.id in entryB.rules.map { it.id } || b.id in entryA.rules.map { it.id }) return true
                        // No common values
                        val valuesA = possibleValues(a, k, this)
                        val valuesB = possibleValues(b, k, this)
                        val commonValues = valuesA.intersect(valuesB)
                        if (commonValues.isEmpty()) return true
                    }
                }
            }
        }
        return false
    }
}

interface Evaluator {
    fun possibleValues(parent: Constraint, entryIndex: Int, constraints: List<Constraint>): Set<Int>
}

interface Matcher {
    fun findMatch(constraints: List<Constraint>): Pair<Int, Int>?
}

// Model

class Constraint(val id: Int, vararg entries: Entry) {

    val entries = if (entries.size == CONSTRAINT_TYPES) arrayOf(*entries) else
        throw IllegalArgumentException("Wrong number of arguments")

    companion object {

        private const val CONSTRAINT_TYPES = 6
        private const val CONSTRAINT_VARIANTS = 5

        val defaultVariants: Set<Int> = (List(CONSTRAINT_VARIANTS) { it }).toSet()
    }
}

sealed class Entry {
    object None : Entry()
    class Value(val v: Int) : Entry()
    class RuleSet(val rules: Set<Rule>) : Entry()
}

class Rule(var relation: Relation, var id: Int)

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
    val merger = Simplifier().apply {
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
    for (c in merger.constraints) {
        println(c.show())
    }
}
