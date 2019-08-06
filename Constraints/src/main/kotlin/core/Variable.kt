package core

/**
 * Helper class for variable definition.
 *
 * Using this class is optional, since any hashable object,
 * including plain strings and integers, may be used as variables.
 *
 * @param name Generic variable name for problem-specific purposes.
 */
data class Variable(val name: String) {

    override fun toString(): String {
        return name
    }

    companion object {

        val unassigned = Variable("Unassigned")
    }
}
