public class TypeReflection {

    private static final class Foo {
        Foo(Class<?> klass, Object arg) {
            if (klass.equals(String.class) & klass.isInstance(arg)) {
                String v = (String) arg;
                System.out.println(v);
            }
            if (klass.equals(Boolean.class) && klass.isInstance(arg)) {
                Boolean v = (Boolean) arg;
                System.out.println(v);
            }
        }
    }

    public static void main(String[] args) {
        new Foo(String.class, "OK");
        new Foo(Boolean.class, true);
        new Foo(String.class, 10);

        GenericClass<?> gc = new GenericClass<>("", "");
        gc.showType();
    }
}
