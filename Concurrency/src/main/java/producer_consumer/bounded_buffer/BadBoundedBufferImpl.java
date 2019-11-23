package producer_consumer.bounded_buffer;

// https://www.hillelwayne.com/post/augmenting-agile/
class BadBoundedBufferImpl implements BoundedBuffer {
    private static final int CAPACITY = 4;

    private Object[] items = new Object[CAPACITY];
    private int head;
    private int tail;
    private int size;

    @Override
    public synchronized void put(Object x) throws InterruptedException {
        while (size == CAPACITY) {
            wait();
        }
        notify();
        ++size;
        head %= CAPACITY;
        items[head++] = x;
    }

    @Override
    public synchronized Object take() throws InterruptedException {
        while (size == 0) {
            wait();
        }
        notify();
        --size;
        tail %= CAPACITY;
        return items[tail++];
    }
}