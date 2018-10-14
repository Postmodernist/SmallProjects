package typereflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class Generic<T> {

    private T v1;
    private Object v2;

    Generic(T v1, Object v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    void showType() {
        Type type =((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        System.out.println(type.getTypeName());
        if (v1.getClass().isInstance(v2)) {
            System.out.println("Types OK");
        } else {
            System.out.println("Types do not match.");
        }
    }
}