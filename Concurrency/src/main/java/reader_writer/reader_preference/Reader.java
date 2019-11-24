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
        readMutex.lockInterruptibly();
        // BEGIN CRITICAL SECTION
        readerCount++;
        if (readerCount == 1) {
            resource.acquire();
        }
        // END CRITICAL SECTION
        readMutex.unlock();

        String s = doRead();
        readCount.incrementAndGet();

        readMutex.lock();
        // BEGIN CRITICAL SECTION
        readerCount--;
        if (readerCount == 0) {
            resource.release();
        }
        // END CRITICAL SECTION
        readMutex.unlock();

        return s;
    }

    private String doRead() throws IOException {
        String s;
        try (FileInputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = is.read(buffer);
            if (length > 0) {
                byte[] result = new byte[length];
                System.arraycopy(buffer, 0, result, 0, length);
                s = new String(result);
            } else {
                s = null;
            }
        }
        return s;
    }
}
