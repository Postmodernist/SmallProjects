package core

data class Variable(val name: String) {
    override fun toString(): String {
        return name
    }

    companion object {
        val unassigned = Variable("Unassigned")
    }
}
