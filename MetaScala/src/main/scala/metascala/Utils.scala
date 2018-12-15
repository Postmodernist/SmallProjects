package metascala

//noinspection TypeAnnotation
object Utils {

  def to[T, VT](implicit fn: TypeToValue[T, VT]) = fn()

  def value[T]: T = null.asInstanceOf[T]

  trait SubType[T1 <: T2, T2]

  trait Equal[T1 >: T2 <: T2, T2]

  final class Invalid

  class Fn1Wrapper[T1, R](fn: T1 => R) {
    def apply(a1: T1) = fn(a1)
  }

  class Fn2Wrapper[T1, T2, R](fn: (T1, T2) => R) {
    def apply(a1: T1, a2: T2) = fn(a1, a2)
  }

  case class TypeToValue[T, VT](value: VT) {
    def apply() = value
  }
}
