package hlist;

import java.util.function.BiFunction;

import static hlist.HList.*;

@SuppressWarnings("WeakerAccess")
public class HAppend<L, R, LR> {
    private final BiFunction<L, R, LR> append;

    private HAppend(final BiFunction<L, R, LR> append) {
        this.append = append;
    }

    /**
     * Appending the empty list.
     */
    public static <L extends HList<L>> HAppend<HNil, L, L> append() {
        return new HAppend<>((hNil, l) -> l);
    }

    /**
     * Append nonempty list.
     */
    public static <X, A extends HList<A>, B, C extends HList<C>, H extends HAppend<A, B, C>>
    HAppend<HCons<X, A>, B, HCons<X, C>> append(final H h) {
        return new HAppend<>((c, l) -> cons(c.left(), h.append(c.right(), l)));
    }

    public LR append(final L l, final R r) {
        return append.apply(l, r);
    }
}
