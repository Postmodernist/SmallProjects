package producer_consumer.bounded_buffer;

public interface BoundedBuffer {
    void put(Object o) throws InterruptedException;
    Object take() throws InterruptedException;
}
