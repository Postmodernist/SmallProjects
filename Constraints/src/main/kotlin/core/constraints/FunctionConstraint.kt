package core.constraints

import core.Constraint
import core.Domain

/**
 * Constraint which wraps a function defining the constraint logic.
 *
 * Examples:
 * ```
 *     problem = Problem()
 *     problem.addVariables(listOf("a", "b"), listOf(1, 2))
 *     fun func(args: List<Int>) = args[1]!! > args[2]!!
 *     problem.addConstraint(func, listOf("a", "b"))
 *     problem.getSolution()
 * ```
 * Output:
 * ```
 *     {a=1, b=2}
 *
 *     problem = Problem()
 *     problem.addVariables(listOf("a", "b"), listOf(1, 2))
 *     fun func(args: List<Int>) = args[1]!! > args[2]!!
 *     problem.addConstraint(FunctionConstraint(func), listOf("a", "b"))
 *     problem.getSolution()
 * ```
 * Output:
 * ```
 *     {a=1, b=2}
 * ```
 *
 * @param func Function wrapped and queried for constraint logic.
 * @param assigned Whether the function may receive unassigned
 *        variables or not.
 */
class FunctionConstraint<V : Any, D : Any>(
    private val func: (List<D?>) -> Boolean,
    private val assigned: Boolean = true
) : Constraint<V, D> {

    override fun invoke(
        variables: List<V>,
        domains: HashMap<V, Domain<D>>,
        assignments: HashMap<V, D>,
        forwardcheck: Boolean
    ): Boolean {
        val args = List(variables.size) { i -> assignments[variables[i]] }
        val missing = args.count { it == null }
        return if (missing != 0) {
            (assigned || func(args)) &&
                    (!forwardcheck || missing != 1 || forwardCheck(variables, domains, assignments))
        } else {
            func(args)
        }
    }
}
