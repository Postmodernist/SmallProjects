package classes

import javax.inject.Inject

class Bar {
    @Inject
    lateinit var strings: Set<String>

    @Inject
    lateinit var foos: Set<@JvmSuppressWildcards IFoo>
}
