package interfaces

import core.Variable

interface Solver<V> : Iterable<Map<Variable, V>> {

    fun getSolution(
        domains: Map<Variable, List<V>>,
        constraints: List<Pair<(Array<Variable>) -> Boolean, Array<Variable>>>,
        vconstraints: Map<Variable, Pair<(Array<Variable>) -> Boolean, Array<Variable>>>
    )


}