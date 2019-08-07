import core.Problem
import core.constraints.AllDifferentConstraint
import core.solvers.BacktrackingSolver

fun main() {
    val problem = Problem<String, Int>(BacktrackingSolver(true))
    problem.addVariables(listOf("a", "b"), listOf(1, 2, 3))
    problem.addConstraint(AllDifferentConstraint())
    val a = problem.getSolutions()
    println(a)
}
