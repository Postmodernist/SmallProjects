package typereflection;

public class TypeReflectionMain {

    public static void main(String[] args) {
        new Heterogenic(String.class, "OK");
        new Heterogenic(Boolean.class, true);
        new Heterogenic(String.class, 10);

        Generic<?> gc = new Generic<>("", "");
        gc.showType();
    }
}
