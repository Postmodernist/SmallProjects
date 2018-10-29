import java.util.HashMap;
import java.util.Map;

public class ClassMap {

    private static Map<Class, String> classMap = new HashMap<>();

    public static void main(String[] args) {
        Integer a = 123;
        Boolean b = true;
        String s = "str";

        classMap.put(Integer.class, "Integer");
        classMap.put(Boolean.class, "Boolean");
        classMap.put(String.class, "String");

        System.out.println(classMap.get(((Number) a).getClass()));
        System.out.println(classMap.get(b.getClass()));
        System.out.println(classMap.get(s.getClass()));
        System.out.println(classMap.get(Double.class));  // null
    }
}
