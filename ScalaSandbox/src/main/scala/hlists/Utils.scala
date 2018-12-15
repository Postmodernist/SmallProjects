package hlists

object Utils {

  final class Invalid

  class Fn1Wrapper[T1, R](fn: T1 => R) {
    def apply(a1: T1): R = fn(a1)
  }

  class Fn2Wrapper[T1, T2, R](fn: (T1, T2) => R) {
    def apply(a1: T1, a2: T2): R = fn(a1, a2)
  }

  case class TypeToValue[T, VT](value: VT) {
    def apply(): VT = value
  }

}
