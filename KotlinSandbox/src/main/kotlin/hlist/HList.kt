package hlist

/** Heterogeneous list. */
sealed class HList<A : HList<A>>

val nil: HNil
    get() = HNil

/** Constructs a list. */
fun <E, L : HList<L>> cons(e: E, l: L): HCons<E, L> {
    return HCons(e, l)
}

/** Constructs a list containing a single element. */
fun <E> single(e: E): HCons<E, HNil> {
    return cons(e, nil)
}

/** The concatenation of two heterogeneous lists. */
class HAppend<L, R, LR> private constructor(private val f: (L, R) -> LR) {

    fun append(l: L, r: R): LR = f(l, r)

    companion object {

        /** Appends empty list. */
        fun <L : HList<L>> append(): HAppend<HNil, L, L> = HAppend { _, l -> l }

        /** Appends nonempty list. */
        fun <X, A : HList<A>, B, C : HList<C>, H : HAppend<A, B, C>> append(h: H): HAppend<HCons<X, A>, B, HCons<X, C>> =
                HAppend { c, l -> cons(c.head, h.append(c.tail, l)) }
    }
}

/** The nonempty list. */
class HCons<E, L : HList<L>>(val head: E, val tail: L) : HList<HCons<E, L>>()

/** The empty list. */
object HNil : HList<HNil>()
