package typereflection;

class Heterogenic {
    Heterogenic(Object arg) {
        if (arg instanceof String) {
            String v = (String) arg;
            System.out.println(v);
        } else if (arg instanceof Boolean) {
            Boolean v = (Boolean) arg;
            System.out.println(v);
        } else {
            System.out.println("Unknown type.");
        }
    }
}
