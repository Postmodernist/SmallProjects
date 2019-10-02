package various

/*
Given the number of digits used to enumerate pages in the book,
how many pages are in the book?
*/

fun pages(digits: Int, k: Int = 9, n: Int = 1): Int {
    return if (digits < k * n) {
        digits / n
    } else {
        k + pages(digits - k * n, k * 10, n + 1)
    }
}

fun pagesBf(digits: Int): Int {
    var d = digits
    var n = 0
    while (d > 0) {
        n++
        d -= decimals(n)
    }
    return n
}

fun decimals(i: Int): Int {
    var j = i
    var n = 1
    while (j > 9) {
        n++
        j /= 10
    }
    return n
}

fun main() {
    println(pages(2775))
    println(pagesBf(2775))
}
