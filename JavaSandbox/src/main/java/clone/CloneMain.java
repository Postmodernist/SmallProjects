package clone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloneMain {
    public static void main(String[] args) {
        ArrayList<Double> ld = new ArrayList<>(Arrays.asList(3.14159, 2.71828));
        MyCloneable<List<Double>> foo = new MyCloneable<>(42, "Hello", ld);
        MyCloneable<List<Double>> bar = null;
        try {
            bar = foo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        System.out.println(ld);
        System.out.println(foo);
        System.out.println(bar);

        // Modify original list
        ld.add(12.21);
        System.out.println(ld);
        System.out.println(foo);
        System.out.println(bar);
    }
}
