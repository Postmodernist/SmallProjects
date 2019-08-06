import core.Problem

fun main() {
    val problem = Problem<String, Int>()
    problem.addVariables(listOf("a", "b"), listOf(1, 2, 3))
    val a = problem.getSolutions()
    println(a)
}
