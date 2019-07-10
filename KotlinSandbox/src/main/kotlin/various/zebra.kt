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
import various.Nations.*
import various.Pets.*
import java.util.*

class Merger {

    val constraints = HashSet<Constraint>()

    fun add(constraint: Constraint) {
        var done = false
        while (!done) {
            done = true
            for (c in constraints) {
                if (c.intersectionMatches(constraint)) {
                    done = false
                    constraint.merge(c, constraints)
                    remove(c)
                    break
                }
            }
        }
        constraints += constraint
    }

    fun remove(constraint: Constraint) {
        TODO()
    }

    fun merge() {
        while (tryMerge()) {
        }
    }

    private fun tryMerge(): Boolean {
        for (c in constraints) {
            if (c.position == 0) {
                val pp = c.possiblePositions()
                if (pp.size == 1) {
                    c.position = pp[0]
                    add(c)
                    return true
                }
            }
        }
        for (a in constraints) {
            for (i in a.data.indices) {
                if (a.data[i] == 0) {
                    var match: Constraint? = null
                    for (b in constraints) {
                        if (b.data[i] == 0) continue
                        if (a.disjoint(b) && (a.position == 0 || b.possiblePositions().contains(a.position))) {
                            if (match != null) {
                                match = null
                                break
                            } else {
                                match = b
                            }
                        }
                    }
                    if (match != null) {
                        a.merge(match)
                        add(a)
                        return true
                    }
                }
            }
        }
        return false
    }

    class Constraint(private val id: Int, vararg data: Entry) {

        val data =
                if (data.size == CONSTRAINT_TYPES) arrayOf(*data)
                else throw IllegalArgumentException("Wrong number of arguments")

        fun disjoint(other: Constraint): Boolean {
            for (i in data.indices) {
                if (data[i] !is None && other.data[i] !is None) return false
            }
            return true
        }

        fun intersectionMatches(other: Constraint): Boolean {
            for (i in data.indices) {
                val entry = data[i]
                val otherEntry = other.data[i]
                if (entry is Value && otherEntry is Value && entry.v == otherEntry.v) return true
            }
            return false
        }

        fun merge(other: Constraint, constraints: Set<Constraint>) {
            for (i in data.indices) {
                data[i] = when (val entry = data[i]) {
                    is None -> other.data[i]
                    is Value -> entry
                    is Rule -> when (val otherEntry = other.data[i]) {
                        is None -> entry
                        is Value -> otherEntry
                        is Rule -> entry.merge(otherEntry, constraints)
                        is RuleSet -> entry.merge(otherEntry, constraints)
                    }
                    is RuleSet -> when (val otherEntry = other.data[i]) {
                        is None -> entry
                        is Value -> otherEntry
                        is Rule -> entry.merge(otherEntry, constraints)
                        is RuleSet -> entry.merge(otherEntry, constraints)
                    }
                }
            }
        }

        private fun Rule.merge(other: Rule, constraints: Set<Constraint>): Entry {
            return RuleSet(setOf(this)).merge(RuleSet(setOf(other)), constraints)
        }

        private fun Rule.merge(other: RuleSet, constraints: Set<Constraint>): Entry {
            return RuleSet(setOf(this)).merge(other, constraints)
        }

        private fun RuleSet.merge(other: Rule, constraints: Set<Constraint>): Entry {
            return merge(RuleSet(setOf(other)), constraints)
        }

        private fun RuleSet.merge(other: RuleSet, constraints: Set<Constraint>): Entry {
            val newRuleSet = RuleSet(rules + other.rules)
            val result = newRuleSet.possibleValues(constraints)
            return when {
                result.isEmpty() -> throw IllegalStateException("Constraints can't be satisfied")
                result.size == 1 -> Value(result.first())
                else -> newRuleSet
            }
        }

        private fun RuleSet.possibleValues(constraints: Set<Constraint>): Set<Int> {
            var values: Set<Int> = HashSet(List(CONSTRAINT_VARIANTS) { it })
            for (rule in rules) {
                values = values.intersect(rule.possibleValues(constraints))
            }
            return values
        }

        private fun Rule.possibleValues(constraints: Set<Constraint>): Set<Int> {
            val c = constraints.find { it.id == id } ?: throw IllegalStateException("Id $id not found")
            return f(c)
        }
    }

    sealed class Entry {
        object None : Entry()
        class Value(val v: Int) : Entry()
        class Rule(val f: (Constraint) -> Set<Int>, val id: Int) : Entry()
        class RuleSet(val rules: Set<Rule>) : Entry()
    }

    companion object {
        const val CONSTRAINT_TYPES = 6
        const val CONSTRAINT_VARIANTS = 5
    }
}

enum class Colors {
    RED, GREEN, IVORY, YELLOW, BLUE;

    operator fun invoke() = ordinal + 1
}

enum class Nations {
    ENGLISHMAN, SPANIARD, UKRAINIAN, JAPANESE, NORWEGIAN;

    operator fun invoke() = ordinal + 1
}

enum class Pets {
    DOG, SNAILS, FOX, HORSE, ZEBRA;

    operator fun invoke() = ordinal + 1
}

enum class Drinks {
    COFFEE, TEA, MILK, ORANGE_JUICE, WATER;

    operator fun invoke() = ordinal + 1
}

enum class Cigarettes {
    OLD_GOLD, KOOLS, CHESTERFIELDS, LUCKY_STRIKE, PARLIAMENTS;

    operator fun invoke() = ordinal + 1
}

fun Constraint.show(): String {
    val pos = if (data[0] is Value) (data[0] as Value).v.toString() else "?"
    val col = if (data[1] is Value) Colors.values()[(data[1] as Value).v].name else "?"
    val nat = if (data[2] is Value) Nations.values()[(data[2] as Value).v].name else "?"
    val pet = if (data[3] is Value) Pets.values()[(data[3] as Value).v].name else "?"
    val dri = if (data[4] is Value) Drinks.values()[(data[4] as Value).v].name else "?"
    val cig = if (data[5] is Value) Cigarettes.values()[(data[5] as Value).v].name else "?"
    return "[$pos, $col, $nat, $pet, $dri, $cig]"
}

fun main() {
    val merger = Merger().apply {
        // position, color, nation, pet, drink, cigarettes
        add(Constraint(0, RED(), ENGLISHMAN(), 0, 0, 0))
        add(Constraint(0, 0, SPANIARD(), DOG(), 0, 0))
        add(Constraint(0, GREEN(), 0, 0, COFFEE(), 0))
        add(Constraint(0, 0, UKRAINIAN(), 0, TEA(), 0))
        add(
                Constraint(0, IVORY(), 0, 0, 0, 0),
                Constraint(0, GREEN(), 0, 0, 0, 0),
                true
        )
        add(Constraint(0, 0, 0, SNAILS(), 0, OLD_GOLD()))
        add(Constraint(0, YELLOW(), 0, 0, 0, KOOLS()))
        add(Constraint(3, 0, 0, 0, MILK(), 0))
        add(Constraint(1, 0, NORWEGIAN(), 0, 0, 0))
        add(
                Constraint(0, 0, 0, 0, 0, CHESTERFIELDS()),
                Constraint(0, 0, 0, FOX(), 0, 0)
        )
        add(
                Constraint(0, 0, 0, 0, 0, KOOLS()),
                Constraint(0, 0, 0, HORSE(), 0, 0)
        )
        add(Constraint(0, 0, 0, 0, ORANGE_JUICE(), LUCKY_STRIKE()))
        add(Constraint(0, 0, JAPANESE(), 0, 0, PARLIAMENTS()))
        add(
                Constraint(0, 0, NORWEGIAN(), 0, 0, 0),
                Constraint(0, BLUE(), 0, 0, 0, 0)
        )
        add(Constraint(0, 0, 0, ZEBRA(), 0, 0))
        add(Constraint(0, 0, 0, 0, WATER(), 0))
    }

    merger.merge()

    for (c in merger.constraints.sortedBy { it.position }) {
        println(c.show())
    }
}
