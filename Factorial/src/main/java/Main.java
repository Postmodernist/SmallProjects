public class Main {

  @FunctionalInterface
  private interface IFact {
    int invoke(IFact self, int n);
  }

  @SuppressWarnings("TrivialFunctionalExpressionUsage")
  private static int fact(int n) {
    return ((IFact) (self, m) -> self.invoke(self, m)).invoke(
        (self, m) -> m == 1 ? 1 : m * self.invoke(self, m - 1), n);
  }

  public static void main(String[] args) {
    assert fact(5) == 120;
    assert fact(8) == 40320;
    System.out.println("OK");
  }
}
