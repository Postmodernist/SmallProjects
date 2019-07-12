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
import various.Merger.Constraint
import various.Merger.Entry.*
import various.Merger.Rule
import various.Nations.*
import various.Pets.*
import java.util.*

class Merger {

    val constraints = ArrayList<Constraint>()

    fun add(a: Constraint) {
        constraints += a
    }

    fun merge() {
        var done = false
        while (!done) {
            hardMerge()
            val a = resolveRules()
            val b = softMerge()
            done = !a && !b
        }
    }

    private fun hardMerge() {
        println("--- Hard merge ---\n")
        var i = 0
        var cc = ArrayList(constraints)
        while (i < cc.lastIndex) {
            val a = cc[i]
            for (j in i + 1 until cc.size) {
                val b = cc[j]
                if (a.hardMatch(b)) {
                    merge(a, b)
                }
            }
            cc = ArrayList(constraints)
            i++
        }
    }


    private fun softMerge(): Boolean {
        println("--- Soft merge ---\n")
        var modified = false
        var i = 0
        while (i < constraints.size) {
            val a = constraints[i]
            var b: Constraint? = null
            for (j in 0 until constraints.size) {
                val c = constraints[j]
                if (c === a) continue
                if (a.softMatch(c, constraints)) {
                    if (b == null) {
                        b = c
                    } else {
                        b = null
                        break
                    }
                }
            }
            if (b != null) {
                merge(a, b)
                modified = true
            }
            i++
        }
        return modified
    }

    private fun merge(a: Constraint, b: Constraint) {
        println("${a.show()} + ${b.show()}")
        a.merge(b, constraints)
        constraints.remove(b)
        updateRules(a.id, b.id)
        println("    = ${a.show()}\n")
    }

    private fun resolveRules(): Boolean {
        println("--- Resolve rules ---\n")
        var modified = false
        var done = false
        while (!done) {
            done = true
            for (c in constraints) {
                for (i in c.entries.indices) {
                    val e = c.entries[i]
                    if (e is RuleSet) {
                        val v = c.resolveRuleSet(e, i, constraints)
                        if (v != null) {
                            println("Resolved entry $i of ${c.show()} to $v\n")
                            c.entries[i] = v
                            done = false
                            modified = true
                        }
                    }
                }
                if (!done) break
            }
        }
        return modified
    }

    private fun updateRules(newId: Int, oldId: Int) {
        for (c in constraints) {
            for (e in c.entries) {
                if (e is RuleSet) e.rules.forEach { it.updateId(newId, oldId) }
            }
        }
    }

    private fun Rule.updateId(newId: Int, oldId: Int) {
        if (id == oldId) id = newId
    }

    class Constraint(val id: Int, vararg entries: Entry) {

        val entries =
                if (entries.size == CONSTRAINT_TYPES) arrayOf(*entries)
                else throw IllegalArgumentException("Wrong number of arguments")

        fun resolveRuleSet(e: RuleSet, i: Int, constraints: List<Constraint>): Value? {
            val values = e.possibleValues(i, constraints)
            return if (values.size == 1) Value(values.first()) else null
        }

        fun softMatch(other: Constraint, constraints: List<Constraint>): Boolean {
            for (i in entries.indices) {
                val a = entries[i]
                val b = other.entries[i]
                when (a) {
                    is Value -> when (b) {
                        is Value -> return false
                        is RuleSet -> if (a.v !in b.possibleValues(i, constraints)) return false
                    }
                    is RuleSet -> when (b) {
                        is Value -> if (b.v !in a.possibleValues(i, constraints)) return false
                        is RuleSet -> {
                            val valsA = a.possibleValues(i, constraints)
                            val valsB = b.possibleValues(i, constraints)
                            if (valsA.intersect(valsB).isEmpty()) return false
                        }
                    }
                }
                if (entries[i] is Value && other.entries[i] is Value) return false
            }
            return true
        }

        fun hardMatch(other: Constraint): Boolean {
            for (i in entries.indices) {
                val entry = entries[i]
                val otherEntry = other.entries[i]
                if (entry is Value && otherEntry is Value && entry.v == otherEntry.v) return true
            }
            return false
        }

        fun merge(other: Constraint, constraints: List<Constraint>) {
            for (i in entries.indices) {
                entries[i] = when (val entry = entries[i]) {
                    is None -> other.entries[i]
                    is Value -> entry
                    is RuleSet -> when (val otherEntry = other.entries[i]) {
                        is None -> entry
                        is Value -> otherEntry
                        is RuleSet -> entry.merge(otherEntry, i, constraints)
                    }
                }
            }
        }

        private fun RuleSet.merge(other: RuleSet, i: Int, constraints: List<Constraint>): Entry {
            val newRuleSet = RuleSet(rules + other.rules)
            val result = newRuleSet.possibleValues(i, constraints)
            return when {
                result.isEmpty() -> throw IllegalStateException("Constraints can't be satisfied")
                result.size == 1 -> Value(result.first())
                else -> newRuleSet
            }
        }

        private fun RuleSet.possibleValues(i: Int, constraints: List<Constraint>): Set<Int> {
            var values: Set<Int> = HashSet(List(CONSTRAINT_VARIANTS) { it })
            for (c in constraints) {
                val e = c.entries[i]
                if (e is Value) {
                    values = values.subtract(setOf(e.v))
                }
            }
            for (rule in rules) {
                values = values.intersect(rule.possibleValues(constraints))
            }
            return values
        }

        private fun Rule.possibleValues(constraints: List<Constraint>): Set<Int> {
            val c = constraints.find { it.id == id } ?: throw IllegalStateException("Id $id not found")
            return f(c)
        }
    }

    sealed class Entry {
        object None : Entry()
        class Value(val v: Int) : Entry()
        class RuleSet(val rules: Set<Rule>) : Entry()
    }

    class Rule(val f: (Constraint) -> Set<Int>, var id: Int)

    companion object {
        const val CONSTRAINT_TYPES = 6
        const val CONSTRAINT_VARIANTS = 5
    }
}

enum class Colors { RED, GREEN, IVORY, YELLOW, BLUE }
enum class Nations { ENGLISHMAN, SPANIARD, UKRAINIAN, JAPANESE, NORWEGIAN }
enum class Pets { DOG, SNAILS, FOX, HORSE, ZEBRA }
enum class Drinks { COFFEE, TEA, MILK, ORANGE_JUICE, WATER }
enum class Cigarettes { OLD_GOLD, KOOLS, CHESTERFIELDS, LUCKY_STRIKE, PARLIAMENTS }

private fun Constraint.show(): String {
    val pos = if (entries[0] is Value) (entries[0] as Value).v.toString() else "?"
    val col = if (entries[1] is Value) Colors.values()[(entries[1] as Value).v].name else "?"
    val nat = if (entries[2] is Value) Nations.values()[(entries[2] as Value).v].name else "?"
    val pet = if (entries[3] is Value) Pets.values()[(entries[3] as Value).v].name else "?"
    val dri = if (entries[4] is Value) Drinks.values()[(entries[4] as Value).v].name else "?"
    val cig = if (entries[5] is Value) Cigarettes.values()[(entries[5] as Value).v].name else "?"
    return "[$pos, $col, $nat, $pet, $dri, $cig]"
}

private val variants: Set<Int> = HashSet(List(5) { it })

private fun immediatelyRight(c: Constraint): Set<Int> {
    val p = c.entries[0]
    return if (p is Value) {
        variants.intersect(setOf(p.v + 1))
    } else {
        variants.subtract(setOf(0))
    }
}

private fun nextTo(c: Constraint): Set<Int> {
    val p = c.entries[0]
    return if (p is Value) {
        variants.intersect(setOf(p.v - 1, p.v + 1))
    } else {
        variants
    }
}

fun main() {
    var rule: RuleSet
    val merger = Merger().apply {
        // position, color, nation, pet, drink, cigarettes
        add(Constraint(100, None, Value(RED.ordinal), Value(ENGLISHMAN.ordinal), None, None, None))
        add(Constraint(101, None, None, Value(SPANIARD.ordinal), Value(DOG.ordinal), None, None))
        add(Constraint(102, None, Value(GREEN.ordinal), None, None, Value(COFFEE.ordinal), None))
        add(Constraint(103, None, None, Value(UKRAINIAN.ordinal), None, Value(TEA.ordinal), None))
        add(Constraint(104, None, Value(IVORY.ordinal), None, None, None, None))
        rule = RuleSet(setOf(Rule(::immediatelyRight, 104)))
        add(Constraint(105, rule, Value(GREEN.ordinal), None, None, None, None))
        add(Constraint(106, None, None, None, Value(SNAILS.ordinal), None, Value(OLD_GOLD.ordinal)))
        add(Constraint(107, None, Value(YELLOW.ordinal), None, None, None, Value(KOOLS.ordinal)))
        add(Constraint(108, Value(3), None, None, None, Value(MILK.ordinal), None))
        add(Constraint(109, Value(1), None, Value(NORWEGIAN.ordinal), None, None, None))
        add(Constraint(110, None, None, None, Value(FOX.ordinal), None, None))
        rule = RuleSet(setOf(Rule(::nextTo, 110)))
        add(Constraint(111, rule, None, None, None, None, Value(CHESTERFIELDS.ordinal)))
        add(Constraint(112, None, None, None, Value(HORSE.ordinal), None, None))
        rule = RuleSet(setOf(Rule(::nextTo, 112)))
        add(Constraint(113, rule, None, None, None, None, Value(KOOLS.ordinal)))
        add(Constraint(114, None, None, None, None, Value(ORANGE_JUICE.ordinal), Value(LUCKY_STRIKE.ordinal)))
        add(Constraint(115, None, None, Value(JAPANESE.ordinal), None, None, Value(PARLIAMENTS.ordinal)))
        add(Constraint(116, None, Value(BLUE.ordinal), None, None, None, None))
        rule = RuleSet(setOf(Rule(::nextTo, 116)))
        add(Constraint(117, rule, None, Value(NORWEGIAN.ordinal), None, None, None))
        add(Constraint(118, None, None, None, Value(ZEBRA.ordinal), None, None))
        add(Constraint(119, None, None, None, None, Value(WATER.ordinal), None))
/*
        add(Constraint(104, Value(3), Value(RED.ordinal), None, None, None, None))
        rule = RuleSet(setOf(Rule(::immediatelyRight, 104)))
        add(Constraint(105, rule, Value(GREEN.ordinal), None, None, None, None))
        rule = RuleSet(setOf(Rule(::nextTo, 104)))
        add(Constraint(106, rule, Value(IVORY.ordinal), None, None, None, None))
*/
    }

    merger.merge()

    for (c in merger.constraints) {
        println(c.show())
    }
}
