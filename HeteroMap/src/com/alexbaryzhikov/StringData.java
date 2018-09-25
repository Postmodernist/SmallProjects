package com.alexbaryzhikov;

public class StringData implements Data<String> {

    @Override
    public void invoke(String data) {
        System.out.println("String: " + data);
    }
}
