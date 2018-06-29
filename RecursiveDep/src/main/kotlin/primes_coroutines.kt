import kotlin.coroutines.experimental.buildSequence

/** Detects primes by lazy sieving out all non-primes and ignoring evens. */
fun primesCr(): Lazy<Sequence<Int>> = lazy {
  buildSequence {
    yieldAll(listOf(2, 3, 5, 7))
    val sieve = mutableMapOf<Int, Int>()
    val basePrimes = primesCr().value.iterator() // generate supply of "base" primes
    var p = basePrimes.apply { next() }.next()          // discard 2 then get 3
    var q = p * p     // square next base prime to keep track of in sieve
    var n = 9             // n is the next candidate number
    while (true) {
      if (n !in sieve) {  // n is not a multiple of any of base primes,
        if (n < q) {      // below next base prime's square, so
          yield(n)        // n is prime
        } else {
          val p2 = p * 2    // n == p * p: for prime p, add p * p + p * 2
          sieve[q + p2] = p2    // to the sieve, with p * 2 as the increment step
          p = basePrimes.next() // pull next base prime
          q = p * p             // and get its square
        }
      } else {
        val s = sieve.remove(n)!!
        var nxt = n + s     // n is a multiple of some base prime, find next multiple
        while (nxt in sieve) {  // ensure each entry is unique
          nxt += s
        }
        sieve[nxt] = s          // nxt is next non-marked multiple of this prime
      }
      n += 2                    // work on odds only
    }
  }
}

fun main(args: Array<String>) {
  primesCr().value
      .drop(10000)
      .first()
      .apply { println(this) }
}
