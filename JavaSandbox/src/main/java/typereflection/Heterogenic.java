package typereflection;

class Heterogenic {
    Heterogenic(Class<?> klass, Object arg) {
        if (klass.equals(String.class) & klass.isInstance(arg)) {
            String v = (String) arg;
            System.out.println(v);
        } else         if (klass.equals(Boolean.class) && klass.isInstance(arg)) {
            Boolean v = (Boolean) arg;
            System.out.println(v);
        } else {
            System.out.println("Unknown type.");
        }
    }
}
