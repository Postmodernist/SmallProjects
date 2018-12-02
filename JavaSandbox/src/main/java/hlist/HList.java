package hlist;

import java.util.function.BiFunction;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HList<A extends HList<A>> {
    private static HNil nil;

    private HList() {
    }

    public static HNil nil() {
        return HNil.nil;
    }

    /**
     * Constructs a list.
     */
    public static <E, L extends HList<L>> HCons<E, L> cons(final E e, final L l) {
        return new HCons<>(e, l);
    }

    /**
     * Constructs a list containing a single element.
     */
    public static <E> HCons<E, HNil> single(final E e) {
        return cons(e, nil());
    }

    /**
     * The concatenation of two heterogeneous lists.
     */
    public static final class HAppend<L, R, LR> {
        private final BiFunction<L, R, LR> append;

        private HAppend(final BiFunction<L, R, LR> append) {
            this.append = append;
        }

        /**
         * Appends empty list.
         */
        public static <L extends HList<L>> HAppend<HNil, L, L> append() {
            return new HAppend<>((hNil, l) -> l);
        }

        /**
         * Appends nonempty list.
         */
        public static <X, A extends HList<A>, B, C extends HList<C>, H extends HAppend<A, B, C>>
        HAppend<HCons<X, A>, B, HCons<X, C>> append(final H h) {
            return new HAppend<>((c, l) -> cons(c.head(), h.append(c.tail(), l)));
        }

        public LR append(final L l, final R r) {
            return append.apply(l, r);
        }
    }

    /**
     * The nonempty list.
     */
    public static final class HCons<E, L extends HList<L>> extends HList<HCons<E, L>> {
        private E e;
        private L l;

        private HCons(final E e, final L l) {
            this.e = e;
            this.l = l;
        }

        public E head() {
            return e;
        }

        public L tail() {
            return l;
        }
    }

    /**
     * The empty list.
     */
    public static final class HNil extends HList<HNil> {
        private static final HNil nil = new HNil();

        private HNil() {
        }
    }
}
