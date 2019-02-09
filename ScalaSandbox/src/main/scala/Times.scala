object Times {

  implicit def IntWithTimes(n: Int): Object {def times[T](f: => T): Unit} = new {
    def times[T](f: => T): Unit = 0 until n foreach { _ => f }
  }

  def main(args: Array[String]): Unit = {
    5 times {
      println(42)
      "foo"
    }
  }
}
