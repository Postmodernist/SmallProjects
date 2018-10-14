package clone;

public class MyCloneable<T> implements Cloneable {
    private int a;
    private String b;
    private T c;

    MyCloneable(int a, String b, T c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    protected MyCloneable<T> clone() throws CloneNotSupportedException {
        return (MyCloneable<T>) super.clone();
    }

    @Override
    public String toString() {
        return "clone.MyCloneable{a='" + a + "', b='" + b + "', c='" + c + "'}";
    }

    public int getA() {
        return a;
    }

    public String getB() {
        return b;
    }
}
