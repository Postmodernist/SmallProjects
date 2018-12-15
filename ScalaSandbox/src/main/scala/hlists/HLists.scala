package hlists

import hlists.Nats.{Nat, NatVisitor, Succ, _0}
import hlists.Utils.{Fn1Wrapper, Fn2Wrapper}

import scala.annotation.tailrec

object HLists {

  type ::[H, T <: HList] = HCons[H, T]
  type NthType[L <: HList, N <: Nat] = N#Accept[NthVisitor[L]]

  val HNil = new HNil()

  def stringValueOf(hlist: HList): String = {

    @tailrec
    def traverse(acc: StringBuilder, lst: HList, i: Nat): Unit = {
      lst match {
        case _: HNil =>
        case HCons(head, tail) =>
          acc.append(head)
          if (tail != HNil) {
            acc.append(", ")
          }
          traverse(acc, tail, Succ(i.toInt + 1))
      }
    }

    val s = new StringBuilder().append("[")
    traverse(s, hlist, _0)
    s.append("]").toString()
  }

  sealed trait HList {
    type Head
    type Tail <: HList
    type Append[L <: HList] <: HList
  }

  final class HNil extends HList {
    type Head = Nothing
    type Tail = HNil
    type Append[L <: HList] = L

    def ::[T](v: T) = HCons(v, this)

    def :::[L <: HList](l: L): L = l
  }

  case class HCons[H, T <: HList](head: H, tail: T) extends HList {
    type This = HCons[H, T]
    type Head = H
    type Tail = T
    type Append[L <: HList] = HCons[H, T#Append[L]]

    def ::[V](v: V) = HCons(v, this)

    def :::[L <: HList](l: L)(implicit fn: AppendFn[L, This]): L#Append[This] = fn(l, this)

    def nth[N <: Nat](implicit fn: NthFn[This, N]): NthType[This, N] = fn(this)

  }

  case class AppendFn[L1 <: HList, L2 <: HList](fn: (L1, L2) => L1#Append[L2]) extends Fn2Wrapper(fn)

  case class NthFn[L <: HList, N <: Nat](fn: L => NthType[L, N]) extends Fn1Wrapper(fn)

  // Append

  implicit def hlistNilAppender[L <: HList]: AppendFn[HNil, L] = AppendFn[HNil, L]((_: HNil, l: L) => l)

  implicit def hlistConsAppender[H, T <: HList, L2 <: HList, R <: HList](implicit fn: AppendFn[T, L2]): AppendFn[HCons[H, T], L2] =
    AppendFn[HCons[H, T], L2]((l1: HCons[H, T], l2: L2) => HCons(l1.head, fn(l1.tail, l2)))

  // Nth

  implicit def hlistConsNth0[H, T <: HList]: NthFn[HCons[H, T], _0] = NthFn[HCons[H, T], _0](l => l.head)

  implicit def hlistConsNth[H, T <: HList, P <: Nat](implicit fn: NthFn[T, P]): NthFn[HLists.HCons[H, T], Succ[P]] =
    NthFn[HCons[H, T], Succ[P]](l => fn(l.tail))


  final class NthVisitor[L <: HList] extends NatVisitor {
    type ResultType = Any
    type Visit0 = L#Head
    type VisitSucc[Pre <: Nat] = Pre#Accept[NthVisitor[L#Tail]]
  }

}
