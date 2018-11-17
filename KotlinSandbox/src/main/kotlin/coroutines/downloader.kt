package coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext

const val N_WORKERS = 4

data class Location(val name: String)

data class Content(val body: String)

data class Reference(val location: Location)

data class LocContent(val loc: Location, val content: Content)

/**
 * Main downloader abstraction.
 * Consumes [Reference]s and incapsulates the downloading and processing machinery.
 */
fun CoroutineScope.processReferences(
        references: ReceiveChannel<Reference>
) {
    val locations = Channel<Location>(Channel.UNLIMITED)
    val contents = Channel<LocContent>(Channel.UNLIMITED)
    repeat(N_WORKERS) { worker(it, locations, contents) }  // create workers
    downloader(references, locations, contents)  // launch downloader agent
}

/**
 * Downloader agent.
 * Consumes [Reference]s, resolves [Location]s and dispatches download jobs to [worker]s.
 * Consumes [LocContent]s from workers and processes it.
 */
fun CoroutineScope.downloader(
        references: ReceiveChannel<Reference>,
        locations: SendChannel<Location>,
        contents: ReceiveChannel<LocContent>
) = launch {
    val requested = mutableMapOf<Location, MutableList<Reference>>()
    while (true) {
        select<Unit> {
            references.onReceive { ref ->
                println("[downloader] Received reference to ${ref.location}'")
                val loc = ref.location
                val refs = requested[loc]
                if (refs == null) {
                    requested[loc] = mutableListOf(ref)
                    locations.send(loc)
                } else {
                    refs.add(ref)
                }
            }
            contents.onReceive { (loc, content) ->
                println("[downloader] Download finished: $loc -> $content")
                val refs = requested.remove(loc)!!
                for (ref in refs) {
                    processContent(ref, content)
                }
            }
        }
    }
}

/**
 * Worker coroutine for downloading content.
 * Consumes [Location]s and emits [LocContent]s.
 */
fun CoroutineScope.worker(
        id: Int,
        locations: ReceiveChannel<Location>,
        contents: SendChannel<LocContent>
) = launch {
    for (loc in locations) {
        println("[worker-$id] Downloading data from ${loc.name}")
        val content = downloadContent(loc)
        println("[worker-$id] Sending $content")
        contents.send(LocContent(loc, content))
    }
}

/** Mocks downloading function. */
suspend fun downloadContent(location: Location): Content {
    delay(300)
    return Content("Data from ${location.name}")
}

/** Mocks content processing. */
fun processContent(reference: Reference, content: Content) {
    println("[processContent] Processing $reference and $content")
}

/** Custom [CoroutineScope] for controlling [processReferences] service. */
class MyCoroutineScope : CoroutineScope {
    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default
}

fun main(args: Array<String>) = runBlocking {
    val references = Channel<Reference>(Channel.UNLIMITED)
    val myCoroutineScope = MyCoroutineScope()
    myCoroutineScope.processReferences(references)
    repeat(10) {
        references.send(Reference(Location("url-$it")))
    }
    delay(5000)
    myCoroutineScope.job.cancel()
}
