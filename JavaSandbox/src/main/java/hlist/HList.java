package hlist;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HList<A extends HList<A>> {
    private static HNil nil;

    private HList() {
    }

    public static HNil nil() {
        if (nil == null) {
            nil = new HNil();
        }
        return nil;
    }

    /**
     * List constructor.
     */
    public static <E, L extends HList<L>> HCons<E, L> cons(final E e, final L l) {
        return new HCons<>(e, l);
    }

    /**
     * Empty list.
     */
    public static final class HNil extends HList<HNil> {
        private HNil() {
        }
    }

    /**
     * Pair.
     */
    public static final class HCons<E, L extends HList<L>> extends HList<HCons<E, L>> {
        private E e;
        private L l;

        private HCons(final E e, final L l) {
            this.e = e;
            this.l = l;
        }

        public E left() {
            return e;
        }

        public L right() {
            return l;
        }
    }
}
