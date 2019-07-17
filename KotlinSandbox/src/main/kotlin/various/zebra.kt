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
    private val evaluator: Evaluator = EvaluatorImpl()
    private val matcher: Matcher = MatcherImpl(evaluator)
    private val merger: Merger = MergerImpl()

    fun add(a: Constraint) {
        constraints += a
    }

    fun simplify(): Simplifier {
        constraints.sortBy { it.id }
        addReciprocalRelations()
        var i = 1
        var modified = true
        while (modified) {
            println("=== Merge cycle ${i++} ===\n")
            modified = false
            val match = matcher.findMatch(constraints)
            if (match != null) {
                val result = merger.merge(match.first, match.second, constraints)
                constraints.apply {
                    val id2 = removeAt(match.second).id
                    val id1 = removeAt(match.first).id
                    updateRules(id1, result.id)
                    updateRules(id2, result.id)
                    add(result)
                    sortBy { it.id }
                }
                modified = true
            }
            if (resolveRules()) {
                modified = true
            }
        }
        return this
    }

    private fun addReciprocalRelations() {
        println("> Add reciprocal relations")
        for (constraint in constraints) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry is RuleSet) {
                    for (rule in entry.rules) {
                        val reciprocalRelation = Relation(rule.relation.g, rule.relation.f)
                        val reciprocalRule = Rule(reciprocalRelation, constraint.id)
                        val otherConstraint = constraints.find { it.id == rule.id }
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

    private fun updateRules(oldId: Int, newId: Int) {
        for (c in constraints) {
            for (e in c.entries) {
                if (e is RuleSet) e.rules.forEach { if (it.id == oldId) it.id = newId }
            }
        }
    }

    private fun resolveRules(): Boolean {
        println("> Resolve rules")
        var modified = false
        loop@ while (true) {
            for (c in constraints) {
                for (i in c.entries.indices) {
                    if (c.resolveEntry(i)) {
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

    private fun Constraint.resolveEntry(i: Int): Boolean {
        if (entries[i] !is RuleSet) return false
        println("Resolve entry $i of ${show()}")
        val values = evaluator.possibleValues(this, i, constraints)
        println("Possible values = ${Arrays.toString(values.toIntArray().apply { sort() })}")
        return when (values.size) {
            0 -> throw IllegalStateException("No possible values found")
            1 -> {
                val v = values.first()
                entries[i] = Value(v)
                println("Entry $i of ${show()} = $v")
                true
            }
            else -> false
        }
    }

    interface Evaluator {
        fun possibleValues(parent: Constraint, entryIndex: Int, constraints: List<Constraint>): Set<Int>
    }

    interface Matcher {
        fun findMatch(constraints: List<Constraint>): Pair<Int, Int>?
    }

    interface Merger {
        fun merge(i: Int, j: Int, constraints: List<Constraint>): Constraint
    }
}

class MatcherImpl(private val evaluator: Simplifier.Evaluator) : Simplifier.Matcher {

    override fun findMatch(constraints: List<Constraint>): Pair<Int, Int>? {
        println("> Find match")
        // Find direct matches.
        for (i in 0 until constraints.lastIndex) {
            for (j in i + 1 until constraints.size) {
                if (directMatch(i, j, constraints)) {
                    println("Found direct match: ${constraints[i].show()} and ${constraints[j].show()}")
                    println()
                    return Pair(i, j)
                }
            }
        }
        // Find indirect matches.
        for (i in constraints.indices) {
            var foundMatch = false
            var jSaved = -1
            loop@ for (j in constraints.indices) {
                if (i == j) continue@loop
                if (indirectMatch(i, j, constraints)) {
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

    private fun directMatch(i: Int, j: Int, constraints: List<Constraint>): Boolean {
        val a = constraints[i]
        val b = constraints[j]
        for (k in a.entries.indices) {
            val entryA = a.entries[k]
            val entryB = b.entries[k]
            if (entryA is Value && entryB is Value && entryA.v == entryB.v) return true
        }
        return false
    }

    private fun indirectMatch(i: Int, j: Int, constraints: List<Constraint>): Boolean {
        val a = constraints[i]
        val b = constraints[j]
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
                        if (entryA.v !in evaluator.possibleValues(b, k, constraints)) return false
                    }
                }
                is RuleSet -> when (entryB) {
                    is Value -> {
                        // A depends on B
                        if (b.id in entryA.rules.map { it.id }) return false
                        // A can't have value of B
                        if (entryB.v !in evaluator.possibleValues(a, k, constraints)) return false
                    }
                    is RuleSet -> {
                        // One depends on the other
                        if (a.id in entryB.rules.map { it.id } || b.id in entryA.rules.map { it.id }) return false
                        // No common values
                        val valuesA = evaluator.possibleValues(a, k, constraints)
                        val valuesB = evaluator.possibleValues(b, k, constraints)
                        val commonValues = valuesA.intersect(valuesB)
                        if (commonValues.isEmpty()) return false
                    }
                }
            }
        }
        return true
    }
}

class MergerImpl : Simplifier.Merger {

    override fun merge(i: Int, j: Int, constraints: List<Constraint>): Constraint {
        println("> Merge")
        val a = constraints[i]
        val b = constraints[j]
        val c = Constraint(a.id, *a.entries)
        for (k in c.entries.indices) {
            c.entries[k] = when (val entry = c.entries[k]) {
                is None -> b.entries[k]
                is Value -> entry
                is RuleSet -> when (val otherEntry = b.entries[k]) {
                    is None -> entry
                    is Value -> otherEntry
                    is RuleSet -> RuleSet(entry.rules.union(otherEntry.rules))
                }
            }
        }
        println("${a.show()} + ${b.show()} = ${c.show()}")
        println()
        return c
    }
}

class EvaluatorImpl : Simplifier.Evaluator {

    override fun possibleValues(
            parent: Constraint,
            entryIndex: Int,
            constraints: List<Constraint>
    ): Set<Int> {
        val entry = parent.entries[entryIndex] as? RuleSet
                ?: throw java.lang.IllegalArgumentException("Entry is not a RuleSet")
        val values: HashSet<Int> = HashSet(Constraint.defaultVariants)
        for (rule in entry.rules) {
            values.retainAll(rule.possibleValues(parent, entryIndex, constraints))
        }
        val valuesToRemove = ArrayList<Int>()
        for (v in values) {
            val c = constraints.find {
                val e = it.entries[entryIndex]
                e is Value && e.v == v
            }
            if (c != null && distinct(parent, c, entryIndex, constraints)) {
                valuesToRemove += v
            }
        }
        values.removeAll(valuesToRemove)
        if (values.isEmpty()) throw IllegalStateException("No possible values found")
        return values
    }

    private fun Rule.possibleValues(
            parent: Constraint,
            entryIndex: Int,
            constraints: List<Constraint>
    ): Set<Int> {
        // Constraint referenced by this rule.
        val c = constraints.find { it.id == id } ?: throw IllegalStateException("Id $id not found")
        // Set of values to apply relation to.
        val values: Set<Int> = when (val e = c.entries[entryIndex]) {
            None -> Constraint.defaultVariants
            is Value -> setOf(e.v)
            is RuleSet -> HashSet(Constraint.defaultVariants).apply {
                for (rule in e.rules) {
                    if (rule.id == parent.id) {
                        // Recursive relation, start with set of all variants.
                        // TODO Apply relations in turns until fixed point is reached.
                        val acc = HashSet<Int>()
                        for (i in Constraint.defaultVariants) {
                            acc.addAll(rule.relation.f(i))
                        }
                        retainAll(acc)
                    } else {
                        val acc = rule.possibleValues(c, entryIndex, constraints)
                        retainAll(acc)
                    }
                }
            }
        }
        val result = HashSet<Int>()
        for (i in values) {
            result.addAll(relation.f(i))
        }
        return result
    }

    private fun distinct(
            a: Constraint,
            b: Constraint,
            entryIndex: Int,
            constraints: List<Constraint>
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
                        if (entryA.v !in possibleValues(b, k, constraints)) return true
                    }
                }
                is RuleSet -> when (entryB) {
                    is Value -> {
                        // A depends on B
                        if (b.id in entryA.rules.map { it.id }) return true
                        // A can't have value of B
                        if (entryB.v !in possibleValues(a, k, constraints)) return true
                    }
                    is RuleSet -> {
                        // One depends on the other
                        if (a.id in entryB.rules.map { it.id } || b.id in entryA.rules.map { it.id }) return true
                        // No common values
                        val valuesA = possibleValues(a, k, constraints)
                        val valuesB = possibleValues(b, k, constraints)
                        val commonValues = valuesA.intersect(valuesB)
                        if (commonValues.isEmpty()) return true
                    }
                }
            }
        }
        return false
    }

}

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

fun <T : Enum<T>> value(v: T) = Value(v.ordinal)

fun value(v: Int) = Value(v)

fun rule(relation: Relation, id: Int): RuleSet = RuleSet(hashSetOf(Rule(relation, id)))

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
