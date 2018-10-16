import java.lang.IllegalStateException

val gpsException = IllegalStateException("GPS is deactivated.")

fun main(args: Array<String>) {
    throw gpsException
}