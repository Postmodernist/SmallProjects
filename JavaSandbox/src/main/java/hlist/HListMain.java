package hlist;

import static hlist.HAppend.append;
import static hlist.HList.*;

public class HListMain {
    public static void main(String[] args) {
        // List construction
        HCons<String, HCons<Integer, HCons<Boolean, HNil>>> l =
                cons("One", cons(2, cons(true, nil())));

        // Type-safe access
        if (l.right().right().left()) {
            System.out.println(l.left().length() == l.right().left());
        }

        // The two lists
        final HCons<String, HCons<Integer, HCons<Boolean, HNil>>> a =
                cons("Foo", cons(3, cons(true, nil())));
        final HCons<Double, HCons<String, HCons<Integer[], HNil>>> b =
                cons(4.0, cons("Bar", cons(new Integer[]{1, 2}, nil())));

        // A lot of type annotation
        final HAppend<HNil, HCons<Double, HCons<String, HCons<Integer[], HNil>>>,
                HCons<Double, HCons<String, HCons<Integer[], HNil>>>> zero = append();
        final HAppend<HCons<Boolean, HNil>, HCons<Double, HCons<String, HCons<Integer[], HNil>>>,
                HCons<Boolean, HCons<Double, HCons<String, HCons<Integer[], HNil>>>>> one = append(zero);
        final HAppend<HCons<Integer, HCons<Boolean, HNil>>, HCons<Double, HCons<String, HCons<Integer[], HNil>>>,
                HCons<Integer, HCons<Boolean, HCons<Double, HCons<String, HCons<Integer[], HNil>>>>>> two = append(one);
        final HAppend<HCons<String, HCons<Integer, HCons<Boolean, HNil>>>,
                HCons<Double, HCons<String, HCons<Integer[], HNil>>>,
                HCons<String, HCons<Integer, HCons<Boolean, HCons<Double, HCons<String, HCons<Integer[], HNil>>>>>>>
                three = append(two);

        // And all of that lets us append one list to the other.
        final HCons<String, HCons<Integer, HCons<Boolean, HCons<Double, HCons<String, HCons<Integer[], HNil>>>>>>
                x = three.append(a, b);

        // And we can access the components of the concatenated list in a type-safe manner
        if (x.right().right().left())
            System.out.println(x.right().right().right().right().right().left()[1] * 2);  // 4
    }
}
