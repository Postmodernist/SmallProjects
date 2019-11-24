package reader_writer.no_starvation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class Writer {
    static AtomicInteger writeCount = new AtomicInteger();

    private final File file;
    private final Semaphore resource;
    private final ReentrantLock serviceQueue;

    Writer(File file, Semaphore resource, ReentrantLock serviceQueue) {
        this.file = file;
        this.resource = resource;
        this.serviceQueue = serviceQueue;
    }

    void write(String data) throws IOException, InterruptedException {
        // begin ENTRY section
        serviceQueue.lockInterruptibly();
        resource.acquire();
        serviceQueue.unlock();
        // end ENTRY section

        // begin CRITICAL section
        doWrite(data);
        writeCount.incrementAndGet();
        // end CRITICAL section

        // begin EXIT section
        resource.release();
        // end EXIT section
    }

    private void doWrite(String data) throws IOException {
        try (FileOutputStream is = new FileOutputStream(file)) {
            is.write(data.getBytes());
        }
    }
}