package metascala

object Integers {

  import Addables._
  import Subtractables._
  import Utils._
  import Visitables._

  type _1 = MSucc[_0]
  type _2 = MSucc[_1]
  type _3 = MSucc[_2]
  type _4 = MSucc[_3]
  type _5 = MSucc[_4]
  type _6 = MSucc[_5]
  type _7 = MSucc[_6]
  type _8 = MSucc[_7]
  type _9 = MSucc[_8]
  type _10 = MSucc[_9]

  val _0 = new _0
  val _1 = new _1
  val _2 = new _2
  val _3 = new _3
  val _4 = new _4
  val _5 = new _5
  val _6 = new _6
  val _7 = new _7
  val _8 = new _8
  val _9 = new _9
  val _10 = new _10

  sealed trait MInt extends Visitable[IntVisitor] with Addable with Subtractable {
    type AddType = MInt
    type SubType = MInt
    type Add[I <: MInt] <: MInt
    type Neg <: MInt
    type Succ <: MInt
    type Pre <: MInt
    type Sub[I <: MInt] <: MInt
  }

  sealed trait Nat extends MInt {
    type Accept[V <: IntVisitor] = AcceptNatVisitor[V]
    type AcceptNatVisitor[V <: NatVisitor] <: V#ResultType
  }

  sealed trait Pos extends Nat

  trait NatVisitor extends TypeVisitor {
    type Visit0 <: ResultType
    type VisitSucc[Pre <: Nat] <: ResultType
  }

  trait IntVisitor extends NatVisitor {
    type VisitNeg[Pos <: Nat] <: ResultType
  }

  final class _0 extends Nat {
    type Add[I <: MInt] = I
    type AcceptNatVisitor[V <: NatVisitor] = V#Visit0
    type Neg = _0
    type Succ = MSucc[_0]
    type Pre = Succ#Neg
    type Sub[I <: MInt] = I#Neg
  }

  final class MSucc[P <: Nat] extends Pos {
    type This = MSucc[P]
    type Add[N <: MInt] = P#Add[N]#Succ
    type AcceptNatVisitor[V <: NatVisitor] = V#VisitSucc[P]
    type Neg = MNeg[This]
    type Pre = P
    type Succ = MSucc[This]
    type Sub[I <: MInt] = Add[I#Neg]
  }

  //noinspection TypeParameterShadow
  final class MNeg[N <: Pos] extends MInt {
    type Add[N <: MInt] = N#Add[N#Neg]#Neg
    type Accept[V <: IntVisitor] = V#VisitNeg[N]
    type Neg = N
    type Succ = N#Pre#Neg
    type Pre = N#Succ#Neg
    type Sub[I <: MInt] = Add[I#Neg]
  }

  implicit val _0ToInt: TypeToValue[_0, Int] = TypeToValue[_0, Int](0)

  implicit def succToInt[P <: Nat](implicit v: TypeToValue[P, Int]): TypeToValue[MSucc[P], Int] = TypeToValue[MSucc[P], Int](1 + v.value)

}
