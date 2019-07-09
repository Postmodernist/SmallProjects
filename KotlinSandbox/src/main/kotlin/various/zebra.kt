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

import java.util.*
import kotlin.collections.ArrayList

class Merger(private val n: Int) {

    val constraints = ArrayList<Constraint>()
    private val positions = TreeSet(List(n) { it + 1 })
    private val neighbours = ArrayList<Neighbour>()

    fun add(constraint: Constraint) {
        var done = false
        while (!done) {
            done = true
            for (c in constraints) {
                if (c.intersectionMatches(constraint)) {
                    done = false
                    constraint.merge(c)
                    constraints.remove(c)
                    break
                }
            }
        }
        constraints += constraint
        if (constraint.position != 0) {
            positions.remove(constraint.position)
        }
        resolveAllNeighbours()
    }

    fun add(a: Constraint, b: Constraint, ordered: Boolean = false) {
        add(a)
        add(b)
        neighbours += Neighbour(a, b, ordered)
        resolveAllNeighbours()
    }

    private fun resolveAllNeighbours() {
        if (positions.isEmpty()) return
        for (n in neighbours) {
            resolveNeighbours(n.first, n.second, n.ordered)
        }
    }

    private fun resolveNeighbours(a: Constraint, b: Constraint, ordered: Boolean) {
        when {
            a.position == 0 && b.position == 0 -> if (ordered && positions.size == 2) {
                a.position = positions.first()
                b.position = a.position + 1
                add(a)
                add(b)
            }
            a.position != 0 -> makeNeighbours(a, b, ordered)
            else -> makeNeighbours(b, a, ordered)
        }
    }

    private fun makeNeighbours(a: Constraint, b: Constraint, ordered: Boolean) {
        when {
            ordered -> {
                b.position = a.position + 1
                add(b)
            }
            positions.contains(a.position - 1) && !positions.contains(a.position + 1) -> {
                b.position = a.position - 1
                add(b)
            }
            positions.contains(a.position + 1) && !positions.contains(a.position - 1) -> {
                b.position = a.position + 1
                add(b)
            }
        }
    }

    fun merge() {
        while (tryMerge());
    }

    private fun tryMerge(): Boolean {
        for (c in constraints) {
            if (c.position == 0) {
                val ps = c.possiblePositions()
                if (ps.size == 1) {
                    c.position = ps[0]
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

    private fun Constraint.possiblePositions(): List<Int> {
        val ns = HashSet<Constraint>()
        for (n in neighbours) {
            if (n.first == this) {
                ns += n.second
            } else if (n.second == this) {
                ns += n.first
            }
        }
        var ps: Set<Int> = HashSet(List(n) { it + 1 })
        for (c in constraints) {
            if (c.position != 0 && ns.contains(c)) {
                val x = setOf(c.position - 1, c.position + 1)
                ps = ps.intersect(x)
            }
        }
        return ps.toList()
    }

    class Constraint(vararg data: Int) {

        val data =
                if (data.size == CONSTRAINT_SIZE) intArrayOf(*data)
                else throw IllegalArgumentException("Wrong number of arguments")

        var position: Int
            get() = data[0]
            set(value) {
                data[0] = value
            }

        fun merge(other: Constraint) {
            for (i in data.indices) {
                if (data[i] == 0) data[i] = other.data[i]
            }
        }

        fun disjoint(other: Constraint): Boolean {
            for (i in data.indices) {
                if (data[i] != 0 && other.data[i] != 0) return false
            }
            return true
        }

        fun intersectionMatches(other: Constraint): Boolean {
            for (i in data.indices) {
                if (data[i] != 0 && data[i] == other.data[i]) return true
            }
            return false
        }
    }

    class Neighbour(
            val first: Constraint,
            val second: Constraint,
            val ordered: Boolean
    )

    companion object {
        const val CONSTRAINT_SIZE = 6
    }
}

enum class Colors { X, RED, GREEN, IVORY, YELLOW, BLUE }
enum class Nations { X, ENGLISHMAN, SPANIARD, UKRAINIAN, JAPANESE, NORWEGIAN }
enum class Drinks { X, COFFEE, TEA, MILK, ORANGE_JUICE, WATER }
enum class Cigarettes { X, OLD_GOLD, KOOLS, CHESTERFIELDS, LUCKY_STRIKE, PARLIAMENTS }
enum class Pets { X, DOG, SNAILS, FOX, HORSE, ZEBRA }

fun Merger.Constraint.show(): String {
    val position = if (data[0] == 0) "X" else data[0].toString()
    val color = Colors.values()[data[1]]
    val nation = Nations.values()[data[2]]
    val drink = Drinks.values()[data[3]]
    val cigarettes = Cigarettes.values()[data[4]]
    val pet = Pets.values()[data[5]]
    return "[$position, $color, $nation, $drink, $cigarettes, $pet]"
}

fun main() {
    val merger = Merger(5).apply {
        // position, color, nation, pet, drink, cigarettes
        add(Merger.Constraint(0, Colors.RED.ordinal, Nations.ENGLISHMAN.ordinal, 0, 0, 0))
        add(Merger.Constraint(0, 0, Nations.SPANIARD.ordinal, Pets.DOG.ordinal, 0, 0))
        add(Merger.Constraint(0, Colors.GREEN.ordinal, 0, 0, Drinks.COFFEE.ordinal, 0))
        add(Merger.Constraint(0, 0, Nations.UKRAINIAN.ordinal, 0, Drinks.TEA.ordinal, 0))
        add(
                Merger.Constraint(0, Colors.IVORY.ordinal, 0, 0, 0, 0),
                Merger.Constraint(0, Colors.GREEN.ordinal, 0, 0, 0, 0),
                true
        )
        add(Merger.Constraint(0, 0, 0, Pets.SNAILS.ordinal, 0, Cigarettes.OLD_GOLD.ordinal))
        add(Merger.Constraint(0, Colors.YELLOW.ordinal, 0, 0, 0, Cigarettes.KOOLS.ordinal))
        add(Merger.Constraint(3, 0, 0, 0, Drinks.MILK.ordinal, 0))
        add(Merger.Constraint(1, 0, Nations.NORWEGIAN.ordinal, 0, 0, 0))
        add(
                Merger.Constraint(0, 0, 0, 0, 0, Cigarettes.CHESTERFIELDS.ordinal),
                Merger.Constraint(0, 0, 0, Pets.FOX.ordinal, 0, 0)
        )
        add(
                Merger.Constraint(0, 0, 0, 0, 0, Cigarettes.KOOLS.ordinal),
                Merger.Constraint(0, 0, 0, Pets.HORSE.ordinal, 0, 0)
        )
        add(Merger.Constraint(0, 0, 0, 0, Drinks.ORANGE_JUICE.ordinal, Cigarettes.LUCKY_STRIKE.ordinal))
        add(Merger.Constraint(0, 0, Nations.JAPANESE.ordinal, 0, 0, Cigarettes.PARLIAMENTS.ordinal))
        add(
                Merger.Constraint(0, 0, Nations.NORWEGIAN.ordinal, 0, 0, 0),
                Merger.Constraint(0, Colors.BLUE.ordinal, 0, 0, 0, 0)
        )
        add(Merger.Constraint(0, 0, 0, Pets.ZEBRA.ordinal, 0, 0))
        add(Merger.Constraint(0, 0, 0, 0, Drinks.WATER.ordinal, 0))
    }

    merger.merge()

    for (c in merger.constraints.sortedBy { it.position }) {
        println(c.show())
    }
}
