package various

import java.text.SimpleDateFormat
import java.util.*


fun main() {
    val utcFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val moscowFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").apply {
        timeZone = TimeZone.getTimeZone("Europe/Moscow")
    }
    val utcDate = utcFormatter.parse("31/10/2019 12:00:00")
    println(moscowFormatter.format(utcDate))
}
