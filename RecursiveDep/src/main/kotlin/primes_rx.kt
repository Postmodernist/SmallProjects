import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class Sieve(var q: Int = 0,  // square of the next base prime to keep track of in the sieve map
            var n: Int = 9,  // next candidate number
            val m: MutableMap<Int, Int> = mutableMapOf())  // sieve

/** Detects primes by lazy sieving out all non-primes and ignoring evens. */
fun primesRx(): Observable<Int> {
  val basePrimes: Observable<Int> = Observable.just(2, 3, 5, 7)
  val sieve = Sieve()
  return Observable.concat(basePrimes, Observable.defer(::primesRx)
      .skip(1)  // discard 2 and get 3
      .flatMap { p ->
        Observable.create<Int> {
          with(sieve) {
            q = p * p                 // get a square of the next base prime
            var complete = false
            while (!complete) {
              if (n !in m) {          // n is not a multiple of any of base primes,
                if (n < q) {          // below next base prime's square, so
                  it.onNext(n)        // n is prime
                } else {
                  val p2 = p * 2  // n == p * p: for prime p, add p * p + p * 2
                  m[q + p2] = p2      // to the sieve, with p * 2 as the increment step
                  complete = true     // pull next base prime
                }
              } else {
                val s = m.remove(n)!!
                var nxt = n + s   // n is a multiple of some base prime, find next multiple
                while (nxt in m) {    // ensure each entry is unique
                  nxt += s
                }
                m[nxt] = s            // nxt is the next non-marked multiple of this prime
              }
              n += 2                  // work on odds only
            }
          }
          it.onComplete()
        }
      })
}

fun main(args: Array<String>) {
  val disposables = CompositeDisposable()

  disposables.add(primesRx()
      .take(25)
      .forEach { print("$it ") })

  println()

  disposables.add(primesRx()
      .skip(999)
      .first(-1)
      .subscribe { t: Int? -> println(t) })

  disposables.dispose()
}
