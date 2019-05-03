package coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private const val N_WORKERS = 4

data class Reference(val id: Int)
data class Location(val id: Int)
data class Content(val id: Int)
data class LocContent(val loc: Location, val content: Content)

fun Reference.resolveLocation(): Location {
    log("Resolving location for $this")
    return Location(id)
}

suspend fun downloadContent(loc: Location): Content {
    log("Downloading $loc")
    delay(10)
    return Content(loc.id)
}

fun processContent(ref: Reference, content: Content) {
    log("Processing $ref $content")
}

private fun log(msg: String) {
    val time = SimpleDateFormat("HH:mm:ss.sss").format(Date())
    println("$time [${Thread.currentThread().name}] $msg")
}

/**
 * Downloader.
 * Consumes [Reference]s, resolves [Location]s and dispatches download jobs to [worker]s.
 */
fun CoroutineScope.downloader(
        requested: MutableMap<Location, MutableList<Reference>>,
        references: ReceiveChannel<Reference>,
        locations: SendChannel<Location>
) = launch {
    for (ref in references) {
        val loc = ref.resolveLocation()
        val refs = requested[loc]
        if (refs == null) {
            requested[loc] = mutableListOf(ref)
            locations.send(loc)
        } else {
            refs.add(ref)
        }
    }
}

/**
 * Worker.
 * Consumes [Location]s and emits [LocContent]s.
 */
fun CoroutineScope.worker(
        locations: ReceiveChannel<Location>,
        contents: SendChannel<LocContent>
) = launch {
    for (loc in locations) {
        val content = downloadContent(loc)
        contents.send(LocContent(loc, content))
    }
}

/**
 * Processor.
 * Consumes [LocContent]s from workers and processes it.
 */
fun CoroutineScope.processor(
        requested: MutableMap<Location, MutableList<Reference>>,
        contents: ReceiveChannel<LocContent>
) = launch {
    for ((loc, content) in contents) {
        val refs = requested.remove(loc) ?: continue
        for (ref in refs) {
            processContent(ref, content)
        }
    }
}

/**
 * Abstraction that incapsulates the downloading and processing machinery.
 */
fun CoroutineScope.processReferences(
        references: ReceiveChannel<Reference>
) {
    val requested = ConcurrentHashMap<Location, MutableList<Reference>>()
    val locations = Channel<Location>()
    val contents = Channel<LocContent>()
    repeat(N_WORKERS) { worker(locations, contents) }
    downloader(requested, references, locations)
    processor(requested, contents)
}

fun main() = runBlocking {
    withTimeout(3000) {
        val references = Channel<Reference>()
        processReferences(references)
        var i = 1
        while (true) {
            references.send(Reference(i++))
        }
    }
}
