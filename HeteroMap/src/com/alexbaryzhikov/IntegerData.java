package com.alexbaryzhikov;

public class IntegerData implements Data<Integer> {

    @Override
    public void call(String data) {
        System.out.println("Integer: " + data);
    }
}
