package core.solvers

import core.ConstraintEnv
import core.Domain
import core.Solver
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Problem solver with backtracking capabilities
 *
 * Examples:
 * ```
 *     result = listOf(mapOf("a" to 1, "b" to 2),
 *                     mapOf("a" to 1, "b" to 3),
 *                     mapOf("a" to 2, "b" to 3))
 *
 *     problem = Problem(BacktrackingSolver())
 *     problem.addVariables(listOf("a", "b"), listOf(1, 2, 3))
 *     problem.addConstraint({ args -> args[1]!! > args[0]!! }, listOf("a", "b"))
 *
 *     solution = problem.getSolution()
 *     println(solution in result)
 * ```
 * Output:
 * ```
 *     true
 *
 *     for (solution in problem.getSolutions())
 *         println(solution in result)
 * ```
 * Output:
 * ```
 *     true
 *     true
 *     true
 *
 *     for (solution in problem.getSolutionSequence())
 *         println(solution in result)
 * ```
 * Output:
 * ```
 *     true
 *     true
 *     true
 * ```
 *
 * @param forwardcheck If false forward checking will not be requested
 *        to constraints while looking for solutions
 */
class BacktrackingSolver<V : Any, D : Any>(
    private val forwardcheck: Boolean = true
) : Solver<V, D> {

    override fun getSolution(
        domains: HashMap<V, Domain<D>>,
        constraints: ArrayList<ConstraintEnv<V, D>>,
        vconstraints: HashMap<V, ArrayList<ConstraintEnv<V, D>>>
    ): Map<V, D>? {
        val solutions = getSolutionSequence(domains, constraints, vconstraints)
        return solutions.first()
    }

    override fun getSolutions(
        domains: HashMap<V, Domain<D>>,
        constraints: ArrayList<ConstraintEnv<V, D>>,
        vconstraints: HashMap<V, ArrayList<ConstraintEnv<V, D>>>
    ): List<Map<V, D>> {
        return getSolutionSequence(domains, constraints, vconstraints).toList()
    }

    override fun getSolutionSequence(
        domains: HashMap<V, Domain<D>>,
        constraints: ArrayList<ConstraintEnv<V, D>>,
        vconstraints: HashMap<V, ArrayList<ConstraintEnv<V, D>>>
    ): Sequence<Map<V, D>> = sequence {

        val assignments: HashMap<V, D> = HashMap()
        val queue: Queue<QueueElement<V, D>> = LinkedList()

        lateinit var variable: V
        lateinit var values: ArrayList<D>
        var pushDomains: ArrayList<Domain<D>>? = null

        while (true) {
            var found = false
            // Mix the Degree and Minimum Remaining Values (MRV) heuristics.
            val prioritizedVariables = ArrayList(domains.keys).apply {
                sortWith(compareBy({ -vconstraints[it]!!.size }, { domains[it]!!.size }))
            }
            for (v in prioritizedVariables) {
                if (v !in assignments) {
                    // Found unassigned variable.
                    variable = v
                    values = ArrayList(domains[v]!!)
                    if (forwardcheck) {
                        pushDomains = ArrayList()
                        for (x in domains.keys) {
                            if (x !in assignments && x != v) {
                                pushDomains.add(domains[x]!!)
                            }
                        }
                    }
                    found = true
                    break
                }
            }

            if (!found) {
                // No unassigned variables. We've got a solution. Go back
                // to last variable, if there's one.
                yield(HashMap(assignments))
                if (queue.isEmpty()) {
                    return@sequence
                }
                val element = queue.poll()
                variable = element.variable
                values = element.values
                pushDomains = element.pushDomains
                pushDomains?.forEach { it.popState() }
            }

            while (true) {
                // We have a variable. Do we have any values left?
                if (values.isEmpty()) {
                    // No. Go back to last variable, if there's one.
                    assignments.remove(variable)
                    found = false
                    while (queue.isNotEmpty()) {
                        val element = queue.poll()
                        variable = element.variable
                        values = element.values
                        pushDomains = element.pushDomains
                        pushDomains?.forEach { it.popState() }
                        if (values.isNotEmpty()) {
                            found = true
                            break
                        }
                        assignments.remove(variable)
                    }
                    if (!found) {
                        return@sequence
                    }
                }

                // Got a value. Check it.
                assignments[variable] = values.removeAt(values.lastIndex)
                pushDomains?.forEach { it.pushState() }
                found = true
                for ((constraint, variables) in vconstraints[variable]!!) {
                    if (!constraint(variables, domains, assignments, forwardcheck)) {
                        // Value is not good.
                        found = false
                        break
                    }
                }
                if (found) {
                    break
                }
                pushDomains?.forEach { it.popState() }
            }
            // Push state before looking for next variable.
            queue.offer(QueueElement(variable, values, pushDomains))
        }
    }

    private data class QueueElement<V : Any, D : Any>(
        val variable: V,
        val values: ArrayList<D>,
        val pushDomains: ArrayList<Domain<D>>?
    )
}