import LongestIncSeq.lis
import LongestIncSeq.lisMemo
import org.scalatest.FunSuite

class LongestIncSeqTest extends FunSuite {
  test("LongestIncSeq.lis") {
    assert(lis(List()) === List())
    assert(lis(List(1)) === List(1))
    assert(lis(List(1, 2, 4)) === List(1, 2, 4))
    assert(lis(List(1, 3, 2, 4)) === List(1, 3, 4))
    assert(lis(List(1, 3, 2, 3)) === List(1, 2, 3))
    assert(lis(List(5, 2, 4)) === List(2, 4))
    assert(lis(List(4, 2, 1)) === List(4))
    assert(lis(List(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)) === List(2, 3, 4, 6, 9))
  }

  test("LongestIncSeq.lisMemo") {
    assert(lisMemo(List()) === List())
    assert(lisMemo(List(1)) === List(1))
    assert(lisMemo(List(1, 2, 4)) === List(1, 2, 4))
    assert(lisMemo(List(1, 3, 2, 4)) === List(1, 3, 4))
    assert(lisMemo(List(1, 3, 2, 3)) === List(1, 2, 3))
    assert(lisMemo(List(5, 2, 4)) === List(2, 4))
    assert(lisMemo(List(4, 2, 1)) === List(4))
    assert(lisMemo(List(7, 2, 1, 3, 8, 4, 9, 1, 2, 6, 5, 9, 3, 8, 1)) === List(2, 3, 4, 6, 9))
  }
}
