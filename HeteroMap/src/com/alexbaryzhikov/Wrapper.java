package com.alexbaryzhikov;

import com.sun.istack.internal.NotNull;

class Wrapper {

    private final StringData string;
    private final IntegerData integer;
    private final String type;

    Wrapper(@NotNull Data<?> data) {
        if (data instanceof StringData) {
            string = (StringData) data;
            integer = null;
            type = "String";
        } else if (data instanceof IntegerData) {
            string = null;
            integer = (IntegerData) data;
            type = "Integer";
        } else {
            string = null;
            integer = null;
            type = "Unknown";
        }
    }

    void invoke(Object data) {
        if (type.equals("String") && string != null) {
            string.invoke((String) data);
        } else if (type.equals("Integer") && integer != null) {
            integer.invoke((Integer) data);
        }
    }
}
