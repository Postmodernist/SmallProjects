package com.alexbaryzhikov;

import com.sun.istack.internal.NotNull;

class Wrapper {

    private Data<String> string = new StringData();
    private Data<Integer> integer = new IntegerData();
    private String type;

    Wrapper(@NotNull Data<?> data) {
        if (data.getClass() == string.getClass()) {
            string = (Data<String>) data;
            type = "String";
        } else if (data.getClass() == integer.getClass()) {
            integer = (Data<Integer>) data;
            type = "Integer";
        }
    }

    void call(String data) {
        if (type.equals("String")) {
            string.call(data);
        } else if (type.equals("Integer")) {
            integer.call(data);
        }
    }
}
