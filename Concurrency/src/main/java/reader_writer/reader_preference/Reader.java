package reader_writer.reader_preference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class Reader {
    private static final int BUFFER_SIZE = 8 * 1024;
    private static int readerCount;

    static AtomicInteger readCount = new AtomicInteger();

    private final File file;
    private final ReentrantLock readMutex;
    private final Semaphore resource;

    Reader(File file, Semaphore resource, ReentrantLock readMutex) {
        this.file = file;
        this.readMutex = readMutex;
        this.resource = resource;
    }

    String read() throws IOException, InterruptedException {
        // begin ENTRY section
        readMutex.lockInterruptibly();
        if (readerCount == 0) {
            resource.acquire();
        }
        readerCount++;
        readMutex.unlock();
        // end ENTRY section

        // begin CRITICAL section
        String s = doRead();
        readCount.incrementAndGet();
        // end CRITICAL section

        // begin EXIT section
        readMutex.lockInterruptibly();
        readerCount--;
        if (readerCount == 0) {
            resource.release();
        }
        readMutex.unlock();
        // end EXIT section

        return s;
    }

    private String doRead() throws IOException {
        String s;
        try (FileInputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = is.read(buffer);
            if (length > 0) {
                s = new String(buffer, 0, length);
            } else {
                s = null;
            }
        }
        return s;
    }
}