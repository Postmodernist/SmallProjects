import scala.annotation.tailrec

object Reverse extends App {

  def reverse(s: String) = s.foldRight("") { (c, s) => s + c }

  println(reverse("reversed"))

  @tailrec
  def reverse(x: Int, y: Int = 0): Int = x match {
    case 0 => y
    case _ if math.abs(y) <= Int.MaxValue / 10 => reverse(x / 10, y * 10 + x % 10)
    case _ => 0
  }

  println(reverse(12345))
  println(reverse(1534236469))
}
