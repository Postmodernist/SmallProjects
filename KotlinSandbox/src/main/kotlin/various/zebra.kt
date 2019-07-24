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

typealias Constraints = HashMap<Int, Constraint>

typealias Model = HashMap<Int, ArrayList<HashSet<Int>>>

interface Simplifier {

    val constraints: Constraints

    fun add(constraint: Constraint)

    fun simplify(): Simplifier
}

interface Cook {

    fun prepare(constraints: Constraints): Model
}

interface HoradricCube {

    fun transmute(
            constraints: Constraints,
            model: Model
    ): HoradricResult
}

sealed class HoradricResult {

    object Contradiction : HoradricResult()

    class Match(val idA: Int, val idB: Int) : HoradricResult()

    object Modified : HoradricResult()

    object Unchanged : HoradricResult()
}

interface Merger {

    fun mergeConstraints(
            constraints: Constraints,
            idA: Int,
            idB: Int
    )

    fun mergeModel(
            model: Model,
            idA: Int,
            idB: Int
    )
}

interface Matcher {

    fun findMatch(constraints: Constraints): Pair<Int, Int>?

    fun findMatch(constraints: Constraints, constraintId: Int, entryIndex: Int, value: Int): Int?
}

class Provider {

    private val matcher: Matcher = MatcherImpl()
    private val merger: Merger = MergerImpl()
    private val cook: Cook = CookImpl(matcher, merger)
    private val horadricCube: HoradricCube = HoradricCubeImpl(matcher, merger)
    private val simplifier: Simplifier = SimplifierImpl(cook, horadricCube, merger)

    fun provideSimplifier(): Simplifier = simplifier
}

class SimplifierImpl(
        private val cook: Cook,
        private val cube: HoradricCube,
        private val merger: Merger
) : Simplifier {

    override val constraints: Constraints = HashMap()

    override fun add(constraint: Constraint) {
        constraints[constraint.id] = constraint
    }

    override fun simplify(): Simplifier {
        val model = cook.prepare(constraints)
        loop@ while (true) {
            when (val result = cube.transmute(constraints, model)) {
                is HoradricResult.Unchanged, HoradricResult.Modified ->
                    break@loop
                is HoradricResult.Contradiction ->
                    throw IllegalStateException("Contradiction")
                is HoradricResult.Match -> {
                    merger.mergeConstraints(constraints, result.idA, result.idB)
                    merger.mergeModel(model, result.idA, result.idB)
                }
            }
        }
        return this
    }
}

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
            val constraintValues = ArrayList<HashSet<Int>>(Constraint.ENTRIES_SIZE)
            for (entry in constraint.entries) {
                val entryValues = if (entry is Value) {
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

class HoradricCubeImpl(
        private val matcher: Matcher,
        private val merger: Merger
) : HoradricCube {

    override fun transmute(constraints: Constraints, model: Model): HoradricResult {
        var transmuteResult: HoradricResult = HoradricResult.Unchanged
        var modified = true
        while (modified) {
            modified = false
            when (val result = model.relaxFixPoint(constraints)) {
                is HoradricResult.Contradiction -> return result
                is HoradricResult.Match -> return result
                is HoradricResult.Modified -> modified = true
            }
            when (val result = model.sieve(constraints)) {
                is HoradricResult.Contradiction -> return result
                is HoradricResult.Match -> return result
                is HoradricResult.Modified -> modified = true
            }
            if (modified) {
                transmuteResult = HoradricResult.Modified
            }
        }
        return transmuteResult
    }

    private fun Model.relaxFixPoint(constraints: Constraints): HoradricResult {
        var modified = false
        var result: HoradricResult
        do {
            result = relax(constraints)
            if (result == HoradricResult.Modified) {
                modified = true
            }
        } while (result == HoradricResult.Modified)
        return when {
            result == HoradricResult.Unchanged && !modified -> HoradricResult.Unchanged
            result == HoradricResult.Unchanged && modified -> HoradricResult.Modified
            else -> result
        }
    }

    private fun Model.relax(constraints: Constraints): HoradricResult {
        var modified = false
        for ((id, constraint) in constraints) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry is RuleSet) {
                    val oldValues = get(id)!![i]
                    val newValues = resolveRules(entry, i)
                    newValues.removeClashes(this, constraints, id, i)
                    if (newValues.isEmpty()) {
                        return HoradricResult.Contradiction
                    }
                    if (newValues != oldValues) {
                        get(id)!![i] = newValues
                        if (newValues.size == 1) {
                            val v = newValues.first()
                            constraint.entries[i] = Value(v)
                            val otherId = matcher.findMatch(constraints, id, i, v)
                            if (otherId != null) {
                                return HoradricResult.Match(id, otherId)
                            }
                        }
                        modified = true
                    }
                }
            }
        }
        return if (modified) HoradricResult.Modified else HoradricResult.Unchanged
    }

    private fun Model.resolveRules(entry: RuleSet, i: Int): HashSet<Int> {
        val entryValues = HashSet(Constraint.defaultVariants)
        for (rule in entry.rules) {
            val ruleValues = HashSet<Int>()
            val refValues = get(rule.id)!![i]
            for (v in refValues) {
                ruleValues.addAll(rule.relation.f(v))
            }
            entryValues.retainAll(ruleValues)
        }
        return entryValues
    }

    private fun HashSet<Int>.removeClashes(
            model: Model,
            constraints: Constraints,
            constraintId: Int,
            entryIndex: Int
    ) {
        val valuesToRemove = ArrayList<Int>()
        loop@ for (v in this) {
            val idB = matcher.findMatch(constraints, constraintId, entryIndex, v) ?: continue
            for (i in 0 until Constraint.ENTRIES_SIZE) {
                if (i == entryIndex) continue
                val valuesA = model[constraintId]!![i]
                val valuesB = model[idB]!![i]
                if (valuesA.intersect(valuesB).isEmpty()) {
                    valuesToRemove.add(v)
                    continue@loop
                }
            }
        }
        removeAll(valuesToRemove)
    }

    private fun Model.sieve(constraints: Constraints): HoradricResult {
        var modified = false
        for ((id, constraint) in constraints) {
            for ((i, entry) in constraint.entries.withIndex()) {
                if (entry !is Value) {
                    val entryValues = get(id)!![i]
                    val valuesToRemove = HashSet<Int>()
                    for (v in entryValues) {
                        val constraintsCopy = constraints.copyConstraints()
                        val modelCopy = copyModel()
                        modelCopy[id]!![i] = hashSetOf(v)
                        var result: HoradricResult
                        do {
                            result = modelCopy.relaxFixPoint(constraints)
                            if (result is HoradricResult.Match) {
                                merger.mergeConstraints(constraintsCopy, result.idA, result.idB)
                                merger.mergeModel(modelCopy, result.idA, result.idB)
                            }
                        } while (result is HoradricResult.Match)
                        if (result == HoradricResult.Contradiction) {
                            valuesToRemove.add(v)
                        }
                    }
                    if (valuesToRemove.isNotEmpty()) {
                        modified = true
                    }
                    entryValues.removeAll(valuesToRemove)
                    if (entryValues.isEmpty()) return HoradricResult.Contradiction
                    if (entryValues.size == 1) {
                        val v = entryValues.first()
                        constraint.entries[i] = Value(v)
                        val otherId = matcher.findMatch(constraints, id, i, v)
                        if (otherId != null) {
                            return HoradricResult.Match(id, otherId)
                        }
                    }
                }
            }
        }
        return if (modified) HoradricResult.Modified else HoradricResult.Unchanged
    }

    private fun Model.copyModel(): Model {
        val modelCopy: Model = HashMap()
        for ((id, constraintValues) in this) {
            val constraintValuesCopy = ArrayList<HashSet<Int>>(Constraint.ENTRIES_SIZE)
            for (entryValues in constraintValues) {
                constraintValuesCopy.add(HashSet(entryValues))
            }
            modelCopy[id] = constraintValuesCopy
        }
        return modelCopy
    }

    private fun Constraints.copyConstraints(): Constraints {
        val constraintsCopy: Constraints = HashMap()
        for ((id, constraint) in this) {
            val entriesCopy = Array<Entry>(Constraint.ENTRIES_SIZE) { None }
            for ((i, entry) in constraint.entries.withIndex()) {
                entriesCopy[i] = when (entry) {
                    is None -> None
                    is Value -> Value(entry.v)
                    is RuleSet -> RuleSet(entry.rules)
                }
            }
            constraintsCopy[id] = Constraint(id, *entriesCopy)
        }
        return constraintsCopy
    }
}

class MergerImpl : Merger {

    override fun mergeConstraints(constraints: Constraints, idA: Int, idB: Int) {
        val a = constraints[idA]!!
        val b = constraints[idB]!!
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
        constraints.remove(idB)
        constraints.updateRules(idB, idA)
    }

    private fun Constraints.updateRules(oldId: Int, newId: Int) {
        for (c in values) {
            for (e in c.entries) {
                if (e is RuleSet) e.rules.forEach { if (it.id == oldId) it.id = newId }
            }
        }
    }

    override fun mergeModel(model: Model, idA: Int, idB: Int) {
        val a = model[idA]!!
        val b = model[idB]!!
        for (i in a.indices) {
            a[i].retainAll(b[i])
        }
        model.remove(idB)
    }
}

class MatcherImpl : Matcher {

    override fun findMatch(constraints: Constraints): Pair<Int, Int>? {
        val ids = constraints.keys.toIntArray()
        for (i in 0 until ids.size - 1) {
            for (j in i + 1 until ids.size) {
                val idA = ids[i]
                val idB = ids[j]
                if (constraints.match(idA, idB)) {
                    return Pair(idA, idB)
                }
            }
        }
        return null
    }

    override fun findMatch(
            constraints: Constraints,
            constraintId: Int,
            entryIndex: Int,
            value: Int
    ): Int? {
        for ((id, constraint) in constraints) {
            if (id == constraintId) continue
            val entry = constraint.entries[entryIndex]
            if (entry is Value && entry.v == value) {
                return id
            }
        }
        return null
    }

    private fun Constraints.match(idA: Int, idB: Int): Boolean {
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
    val simplifier = Provider().provideSimplifier().apply {
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
