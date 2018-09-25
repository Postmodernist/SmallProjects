package com.alexbaryzhikov;

public class IntegerData implements Data<Integer> {

    @Override
    public void invoke(Integer data) {
        System.out.println("Integer: " + data);
    }
}
