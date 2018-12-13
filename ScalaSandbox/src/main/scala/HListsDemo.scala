import HLists.HNil

object HListsDemo extends App {
  val a = true :: 3 :: "Foo" :: HNil
  val b = Array(1, 2) :: "Bar" :: 4.0 :: HNil

  val c = a ::: b

  println(c.head)
  println(c.tail.tail.tail.tail.head)
}
