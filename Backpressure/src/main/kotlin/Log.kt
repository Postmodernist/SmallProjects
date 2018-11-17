object Log {
    fun v(tag: String, msg: String) {
        println("V [$tag] $msg")
    }

    fun d(tag: String, msg: String) {
        println("D [$tag] $msg")
    }

    fun i(tag: String, msg: String) {
        println("I [$tag] $msg")
    }

    fun w(tag: String, msg: String) {
        println("W [$tag] $msg")
    }

    fun e(tag: String, msg: String) {
        println("\u001B[91E [$tag] $msg")
    }

    fun wtf(tag: String, msg: String) {
        println("\u001B[91A [$tag] $msg")
    }
}