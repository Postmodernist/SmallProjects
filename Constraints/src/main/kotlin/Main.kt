import core.Problem
import core.constraints.AllEqualConstraint

fun main() {
    val problem = Problem<String, Int>()
    problem.addVariables(listOf("a", "b", "c"), listOf(1, 2, 3))
    problem.addConstraint(AllEqualConstraint())
    val a = problem.getSolutions()
    println(a)
}
