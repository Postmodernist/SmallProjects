package hlist;

import java.util.Arrays;
import java.util.List;

import static hlist.HList.*;
import static hlist.HList.HAppend.append;

public class HListMain {
    public static void main(String[] args) {
        // List construction
        HCons<List<String>, HCons<Integer, HCons<Boolean, HNil>>> l =
                cons(Arrays.asList("One", "Two"), cons(2, cons(true, nil())));

        // Type-safe access
        if (l.tail().tail().head()) {
            System.out.println(l.head().size() == l.tail().head());  // true
        }

        // The two lists
        final HCons<String, HCons<Integer, HCons<Boolean, HNil>>> a =
                cons("Foo", cons(3, cons(true, nil())));
        final HCons<Double, HCons<String, HCons<List<Integer>, HNil>>> b =
                cons(4.0, cons("Bar", cons(Arrays.asList(1, 2), nil())));

        // A lot of type annotation
        final HAppend<HNil, HCons<Double, HCons<String, HCons<List<Integer>, HNil>>>,
                HCons<Double, HCons<String, HCons<List<Integer>, HNil>>>> zero = append();
        final HAppend<HCons<Boolean, HNil>, HCons<Double, HCons<String, HCons<List<Integer>, HNil>>>,
                HCons<Boolean, HCons<Double, HCons<String, HCons<List<Integer>, HNil>>>>> one = append(zero);
        final HAppend<HCons<Integer, HCons<Boolean, HNil>>, HCons<Double, HCons<String, HCons<List<Integer>, HNil>>>,
                HCons<Integer, HCons<Boolean, HCons<Double, HCons<String, HCons<List<Integer>, HNil>>>>>> two = append(one);
        final HAppend<HCons<String, HCons<Integer, HCons<Boolean, HNil>>>,
                HCons<Double, HCons<String, HCons<List<Integer>, HNil>>>,
                HCons<String, HCons<Integer, HCons<Boolean, HCons<Double, HCons<String, HCons<List<Integer>, HNil>>>>>>>
                three = append(two);

        // And all of that lets us append one list to the other.
        final HCons<String, HCons<Integer, HCons<Boolean, HCons<Double, HCons<String, HCons<List<Integer>, HNil>>>>>>
                x = three.append(a, b);

        // And we can access the components of the concatenated list in a type-safe manner
        if (x.tail().tail().head())
            System.out.println(x.tail().tail().tail().tail().tail().head().get(1) * 2);  // 4
    }
}
