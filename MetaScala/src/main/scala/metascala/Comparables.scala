package metascala

object Comparables {

  import metascala.Booleans._

  type <[C1 <: Comparable, C2 <: C1#CompareType] = C1#LessThan[C2]
  type ==[C1 <: Comparable, C2 <: C1#CompareType] = C1#Equals[C2]

  trait Comparable {
    type CompareType <: Comparable
    type Equals[T <: CompareType] <: Bool
    type LessThan[T <: CompareType] <: Bool
  }

}
