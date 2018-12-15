package tutorial

sealed trait Symbol

sealed trait NoteName

sealed trait Duration

case class Note(name: NoteName, duration: Duration, octave: Int) extends Symbol

case class Rest(duration: Duration) extends Symbol

case object A extends NoteName

case object B extends NoteName

case object C extends NoteName

case object D extends NoteName

case object E extends NoteName

case object F extends NoteName

case object G extends NoteName

case object Whole extends Duration

case object Half extends Duration

case object Quarter extends Duration

object Structures {
  def symbolDuration(symbol: Symbol): Duration =
    symbol match {
      case Note(_, duration, _) => duration
      case Rest(duration) => duration
    }

  def fractionOfWhole(duration: Duration): Double =
    duration match {
      case Whole => 1.0
      case Half => 0.5
      case Quarter => 0.25
    }
}
