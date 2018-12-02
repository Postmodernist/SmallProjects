package typereflection;

public class TypeReflectionMain {
    public static void main(String[] args) {
        new Heterogenic("OK");
        new Heterogenic(true);
        new Heterogenic(10);

        Generic<?> gc = new Generic<>("", "");
        gc.showType();
    }
}
