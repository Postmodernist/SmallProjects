package singleton;

import java.util.Random;

public class MySingleton implements MyInterface {
    private final int id = new Random().nextInt();

    MySingleton() {
    }

    @Override
    public void invoke() {
        System.out.println("I am " + id);
    }
}
