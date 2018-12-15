package metascala

object Addables {

  type +[A1 <: Addable, A2 <: A1#AddType] = A1#Add[A2]

  trait Addable {
    type AddType <: Addable
    type Add[T <: AddType] <: AddType
  }
}
