interface MapSet {
    operator fun <E : Element> get(key: Key<E>): E?

    interface Key<E : Element>

    interface Element : MapSet {
        /** A key of this MapSet element. */
        val key: Key<*>

        override operator fun <E : Element> get(key: Key<E>): E? =
                @Suppress("UNCHECKED_CAST")
                if (this.key == key) this as E else null
    }
}

class MapSetImpl : MapSet {

    private val mutableMap = mutableMapOf<MapSet.Key<*>, MapSet.Element>()

    override fun <E : MapSet.Element> get(key: MapSet.Key<E>): E? {
        @Suppress("UNCHECKED_CAST")
        return mutableMap[key] as E?
    }

    fun put(key: MapSet.Key<*>, element: MapSet.Element) {
        mutableMap[key] = element
    }

}

class User(private val name: String) : MapSet.Element {
    companion object Key : MapSet.Key<User>

    override val key = Key
    override fun toString() = "User(name=$name)"
}

fun main(args: Array<String>) {
    val mapSet = MapSetImpl()
    mapSet.put(User.Key, User("Tom"))
    println(mapSet[User.Key])
}