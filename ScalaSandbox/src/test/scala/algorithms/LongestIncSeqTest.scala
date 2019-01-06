package algorithms

import algorithms.LongestIncSeq.{lis, lisR, myLis, myLisMemo}
import org.scalatest.FunSuite

import scala.collection.mutable

class LongestIncSeqTest extends FunSuite {
  test("LongestIncSeq.myLis") {
    assert(myLis(List()) === List())
    assert(myLis(List(1)) === List(1))
    assert(myLis(List(1, 2, 4)) === List(1, 2, 4))
    assert(myLis(List(1, 3, 2, 4)) === List(1, 3, 4))
    assert(myLis(List(1, 3, 2, 3)) === List(1, 2, 3))
    assert(myLis(List(5, 2, 4)) === List(2, 4))
    assert(myLis(List(4, 2, 1)) === List(4))
    assert(myLis(List(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)) === List(2, 3, 4, 6, 9))
  }

  test("LongestIncSeq.myLisMemo") {
    assert(myLisMemo(List()) === List())
    assert(myLisMemo(List(1)) === List(1))
    assert(myLisMemo(List(1, 2, 4)) === List(1, 2, 4))
    assert(myLisMemo(List(1, 3, 2, 4)) === List(1, 3, 4))
    assert(myLisMemo(List(1, 3, 2, 3)) === List(1, 2, 3))
    assert(myLisMemo(List(5, 2, 4)) === List(2, 4))
    assert(myLisMemo(List(4, 2, 1)) === List(4))
    assert(myLisMemo(List(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)) === List(2, 3, 4, 6, 9))
  }

  test("LongestIncSeq.lisR") {
    val a = Array(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)
    val t = mutable.Map[Int, Int]()
    assert(a.indices.map(lisR(a, _, t)).max === 5)
  }

  test("LongestIncSeq.lis") {
    assert(lis(Array(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)) === List(2, 3, 4, 6, 9))
  }
}
