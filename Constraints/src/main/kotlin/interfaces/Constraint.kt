package interfaces

interface Constraint {

    operator fun invoke(): Boolean {
        return true
    }

    fun preProcess()

    fun forwardCheck()
}