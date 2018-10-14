package singleton;

public class MyInterfaceProvider {
    private static final MyInterface instance = new MySingleton();

    public static MyInterface provideMyInterface() {
        return instance;
    }
}
