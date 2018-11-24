import kotlin.random.Random

object KnownError

data class Person(val id: UUID, val name: String, val year: Int)

data class UUID(val uuid: Int) {
    companion object {
        fun randomUUID() = UUID(Random.nextInt())
    }
}
