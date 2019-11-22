import java.math.BigInteger
import kotlin.math.min
import kotlin.math.sign

class SemanticStringComparator : Comparator<String> {

    override fun compare(first: String?, second: String?): Int {
        return when {
            first == null && second == null -> 0
            first != null && second == null -> 1
            first == null && second != null -> -1
            first != null && second != null -> doCompare(first, second)
            else -> error("Can't happen")
        }
    }

    private fun doCompare(first: String, second: String): Int {
        val firstTokens = TOKENIZER.split(first)
        val secondTokens = TOKENIZER.split(second)

        val minSize = min(firstTokens.size, secondTokens.size)
        for (i in 0 until minSize) {
            val cmp = compareTokens(firstTokens[i], secondTokens[i])
            if (cmp != 0) return cmp
        }
        return firstTokens.size.compareTo(secondTokens.size)
    }

    private fun compareTokens(firstToken: String, secondToken: String): Int {
        var cmp = 0

        if (areNumbers(firstToken, secondToken)) {
            cmp = compareNumerically(firstToken, secondToken)
        }

        if (cmp == 0) {
            cmp = compareLexicographically(firstToken, secondToken)
        }

        return cmp
    }

    private fun areNumbers(firstToken: String, secondToken: String) =
        firstToken.first() in '0'..'9' && secondToken.first() in '0'..'9'

    private fun compareNumerically(firstToken: String, secondToken: String) =
        BigInteger(firstToken).compareTo(BigInteger(secondToken))

    private fun compareLexicographically(firstToken: String, secondToken: String) =
        firstToken.compareTo(secondToken).sign

    companion object {
        private val TOKENIZER = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)".toRegex()
    }
}