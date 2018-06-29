import io.reactivex.Observable

fun numbers(): Observable<Int> = Observable.concat(Observable.range(0, 3), Observable.defer(::numbers)
    .map { it + 3 })

fun main(args: Array<String>) {
  val disposable = numbers()
      .take(10)
      .forEach { println(it) }
  disposable.dispose()
}
