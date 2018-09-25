package com.alexbaryzhikov;

public class Main {

    public static void main(String[] args) {
        Wrapper ws = new Wrapper(new StringData());
        Wrapper wi = new Wrapper(new IntegerData());

        ws.invoke("hello string");
        wi.invoke(42);
    }
}
