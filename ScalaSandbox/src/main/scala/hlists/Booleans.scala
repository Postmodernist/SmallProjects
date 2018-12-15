package hlists

import hlists.Utils.TypeToValue

object Booleans {

  type &&[B1 <: Bool, B2 <: Bool] = B1#And[B2]
  type ||[B1 <: Bool, B2 <: Bool] = B1#Or[B2]

  val True = new True
  val False = new False

  sealed trait Bool {
    type And[B <: Bool] <: Bool
    type Or[B <: Bool] <: Bool
    type Not <: Bool
    type If[IfTrue, IfFalse]
    type If2[T, IfTrue <: T, IfFalse <: T] <: T
  }

  trait IfTrue[P >: True <: True, T] {
    type Type = T
  }

  implicit val falseToBoolean: TypeToValue[False, Boolean] = TypeToValue[False, Boolean](false)
  implicit val trueToBoolean: TypeToValue[True, Boolean] = TypeToValue[True, Boolean](true)

  final class True extends Bool {
    type And[B <: Bool] = B
    type Or[B <: Bool] = True
    type Not = False
    type If[IfTrue, IfFalse] = IfTrue
    type If2[T, IfTrue <: T, IfFalse <: T] = IfTrue
  }

  final class False extends Bool {
    type And[B <: Bool] = False
    type Or[B <: Bool] = B
    type Not = True
    type If[IfTrue, IfFalse] = IfFalse
    type If2[T, IfTrue <: T, IfFalse <: T] = IfFalse
  }

}
