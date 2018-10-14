package singleton;

public class SingletonMain {
    public static void main(String[] args) {
        MyInterface myInterface = MyInterfaceProvider.provideMyInterface();
        myInterface.invoke();
    }
}
