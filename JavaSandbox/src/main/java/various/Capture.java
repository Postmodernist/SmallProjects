package various;

public class Capture {
    public String foo;
    public Holder holder;

    public Capture() {
        holder = new Holder() {
            @Override
            public void invoke() {
                System.out.println(foo);
            }
        };
    }

    public static void main(String[] args) {
        Capture c = new Capture();
        c.holder.invoke();
        c.foo = "hello";
        c.holder.invoke();
    }

    interface Holder {
        void invoke();
    }
}


