import java.util.Arrays;
import java.util.HashSet;

public class Main {
  public static void main(String[] args) {
    InstrumentedSet<String> s = new InstrumentedSet<>(new HashSet<>());
    s.addAll(Arrays.asList("Foo", "Bar", "Baz"));
    System.out.println(s.getAddCount());
  }
}
