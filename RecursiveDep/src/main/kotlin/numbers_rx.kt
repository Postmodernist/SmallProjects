import io.reactivex.Observable

fun numbersRx(): Observable<Int> = Observable.concat(Observable.range(0, 3), Observable.defer(::numbersRx)
    .map { it + 3 })

fun main(args: Array<String>) {
  val disposable = numbersRx()
      .take(10)
      .forEach { println(it) }
  disposable.dispose()
}
