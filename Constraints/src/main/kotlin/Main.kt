import core.constraints.FunctionConstraint

fun main() {
    val c = FunctionConstraint<String, Int>({ args -> args[0]!! > args[1]!! })
    c(
        listOf(),
        hashMapOf(),
        hashMapOf()
    )
}
