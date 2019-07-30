package core

import Constraints
import Model
import interfaces.*
import model.Constraint
import results.HoradricResult.Contradiction
import java.util.*

class ReducerImpl : Reducer {

    private lateinit var copier: Copier
    private lateinit var fitter: Fitter
    private lateinit var merger: Merger
    private lateinit var simplifier: Simplifier

    fun inject(
        copier: Copier,
        fitter: Fitter,
        merger: Merger,
        simplifier: Simplifier
    ) {
        this.copier = copier
        this.fitter = fitter
        this.merger = merger
        this.simplifier = simplifier
    }

    override fun reduce(constraints: Constraints, model: Model): Constraints? {
        if (constraints.size <= Constraint.ENTRY_VARIANTS) {
            return constraints
        }
        var length = constraints.size
        val queue: Queue<Item> = LinkedList()
        val explored = HashSet<Constraints>()
        queue.offer(Item(constraints, model))
        explored.add(constraints)
        while (queue.isNotEmpty()) {
            val item = queue.poll()
            if (item.size < length) {
                length = item.size
                println("Exploring length = $length, queue size = ${queue.size}")
            }
            if (simplifier.simplify(item.constraints, item.model) == Contradiction) {
                continue
            }
            if (item.size <= Constraint.ENTRY_VARIANTS) {
                return item.constraints
            }
            for ((idA, idB) in fitter.fits(item.constraints, item.model)) {
                val constraintsCopy = copier.copyConstraints(item.constraints)
                val modelCopy = copier.copyModel(item.model)
                merger.merge(constraintsCopy, modelCopy, idA, idB)
                if (constraintsCopy !in explored) {
                    explored.add(constraintsCopy)
                    queue.offer(Item(constraintsCopy, modelCopy))
                }
            }
        }
        return null
    }

    private data class Item(val constraints: Constraints, val model: Model) {
        val size: Int
            get() = constraints.size
    }
}
