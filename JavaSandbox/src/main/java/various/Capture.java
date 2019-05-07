package various;

public class Capture {
    private String foo;
    public Holder holder;

    public Capture() {
        int a = 1;

        holder = () -> {
            int b = a;
            System.out.println(Capture.this.foo);
        };
    }

    public static void main(String[] args) {
        Capture c = new Capture();
        c.holder.invoke();
        c.foo = "hello";
        c.holder.invoke();
    }
}

interface Holder {
    void invoke();
}


