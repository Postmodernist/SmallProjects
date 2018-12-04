package hlist

import hlist.HAppend.Companion.append


fun main(args: Array<String>) {
    val l = cons(listOf("One", "Two"), cons(2, cons(true, nil)))
    if (l.tail.tail.head) {
        println(l.head.size == l.tail.head)  // true
    }

    val a = cons("Foo", cons(3, cons(true, nil)))
    val b = cons(4.0, cons("Bar", cons(listOf(1, 2), nil)))

    // A lot of type annotation
    val zero: HAppend<HNil,
            HCons<Double, HCons<String, HCons<List<Int>, HNil>>>,
            HCons<Double, HCons<String, HCons<List<Int>, HNil>>>> = append()
    val one: HAppend<HCons<Boolean, HNil>,
            HCons<Double, HCons<String, HCons<List<Int>, HNil>>>,
            HCons<Boolean, HCons<Double, HCons<String, HCons<List<Int>, HNil>>>>> = append(zero)
    val two: HAppend<HCons<Int, HCons<Boolean, HNil>>,
            HCons<Double, HCons<String, HCons<List<Int>, HNil>>>,
            HCons<Int, HCons<Boolean, HCons<Double, HCons<String, HCons<List<Int>, HNil>>>>>> = append(one)
    val three: HAppend<HCons<String, HCons<Int, HCons<Boolean, HNil>>>,
            HCons<Double, HCons<String, HCons<List<Int>, HNil>>>,
            HCons<String, HCons<Int, HCons<Boolean, HCons<Double, HCons<String, HCons<List<Int>, HNil>>>>>>> = append(two)

    val x = three.append(a, b)

    if (x.tail.tail.head) {
        println(x.tail.tail.tail.tail.tail.head[1] * 2)  // 4
    }
}