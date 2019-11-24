package reader_writer.writer_preference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class Writer {
    private static int writerCount;

    static AtomicInteger writeCount = new AtomicInteger();

    private final File file;
    private final Semaphore resource;
    private final Semaphore readTry;
    private final ReentrantLock writeMutex;

    Writer(File file, Semaphore resource, Semaphore readTry, ReentrantLock writeMutex) {
        this.file = file;
        this.resource = resource;
        this.readTry = readTry;
        this.writeMutex = writeMutex;
    }

    void write(String data) throws IOException, InterruptedException {
        // begin ENTRY section
        writeMutex.lockInterruptibly();
        writerCount++;
        if (writerCount == 1) {
            readTry.acquire();
        }
        writeMutex.unlock();
        // end ENTRY section

        // begin CRITICAL section
        resource.acquire();
        doWrite(data);
        writeCount.incrementAndGet();
        resource.release();
        // end CRITICAL section

        // begin EXIT section
        writeMutex.lockInterruptibly();
        writerCount--;
        if (writerCount == 0) {
            readTry.release();
        }
        writeMutex.unlock();
        // end EXIT section
    }

    private void doWrite(String data) throws IOException {
        try (FileOutputStream is = new FileOutputStream(file)) {
            is.write(data.getBytes());
        }
    }
}
