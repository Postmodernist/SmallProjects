package producer_consumer.bounded_buffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBufferImpl implements BoundedBuffer {
    private static final int CAPACITY = 100;

    private final Lock lock = new ReentrantLock();
    private final Condition full = lock.newCondition();
    private final Condition empty = lock.newCondition();

    private final Object[] items = new Object[CAPACITY];
    private int head;
    private int tail;
    private int size;

    @Override
    public void put(Object x) throws InterruptedException {
        lock.lock();
        try {
            while (size == CAPACITY) {
                full.await();
            }
            items[head] = x;
            head = (head + 1) % CAPACITY;
            size++;
            empty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object take() throws InterruptedException {
        lock.lock();
        try {
            while (size == 0) {
                empty.await();
            }
            Object x = items[tail];
            tail = (tail + 1) % CAPACITY;
            size--;
            full.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}