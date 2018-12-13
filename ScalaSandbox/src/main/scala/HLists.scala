object HLists {
  type ::[H, T <: HList] = HCons[H, T]

  val HNil = new HNil()

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
  }

  case class AppendFn[L1 <: HList, L2 <: HList](fn: (L1, L2) => L1#Append[L2]) {
    def apply(l1: L1, l2: L2): L1#Append[L2] = fn(l1, l2)
  }

  implicit def hlistNilAppender[L <: HList]: AppendFn[HNil, L] = AppendFn[HNil, L]((_: HNil, l: L) => l)

  implicit def hlistConsAppender[H, T <: HList, L2 <: HList, R <: HList](implicit fn: AppendFn[T, L2]): AppendFn[HLists.HCons[H, T], L2] =
    AppendFn[HCons[H, T], L2]((l1: HCons[H, T], l2: L2) => HCons(l1.head, fn(l1.tail, l2)))
}
