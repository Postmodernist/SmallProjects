package metascala

object Subtractables {

  type -[S1 <: Subtractable, S2 <: S1#SubType] = S1#Sub[S2]

  trait Subtractable {
    type SubType <: Subtractable
    type Sub[T <: SubType] <: SubType
  }
}
