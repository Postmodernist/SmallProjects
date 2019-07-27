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

import di.Provider
import model.Constraint
import Cigarettes.*
import Colors.*
import Drinks.*
import model.Entry.*
import model.Relation
import Nations.*
import Pets.*
import Relations.imRight
import Relations.nextTo

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

    val imRight = Relation("imRight", ::imRightF, "imLeft", ::imLeftF)
    val nextTo = Relation("nextTo", ::nextToF, "nextTo", ::nextToF)

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
    val provider = Provider()
    val (constraints, model) = provider.provideCook().apply {
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
    }.prepare()
    provider.provideSimplifier().simplify(constraints, model)
    println()
    for ((_, c) in constraints) {
        println(c.show())
    }
}
