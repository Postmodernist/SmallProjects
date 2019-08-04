import core.constraints.FunctionConstraint

fun main() {
    val c = FunctionConstraint<String, Int>({ xs -> xs[0]!! > xs[1]!! })
    c(
        listOf(),
        hashMapOf(),
        hashMapOf()
    )
}
