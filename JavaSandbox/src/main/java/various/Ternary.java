package various;

public class Ternary {
    public static void main(String[] args) {
        Integer a = null;
        Double b = 1.0;
        Object c = true ? (Object) a : b;
        System.out.println(c);
    }
}
