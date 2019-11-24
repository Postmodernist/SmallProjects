package reader_writer.reader_preference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class Writer {
    static AtomicInteger writeCount = new AtomicInteger();

    private final File file;
    private final Semaphore resource;

    Writer(File file, Semaphore resource) {
        this.file = file;
        this.resource = resource;
    }

    void write(String data) throws IOException, InterruptedException {
        // BEGIN CRITICAL SECTION
        resource.acquire();
        doWrite(data);
        writeCount.incrementAndGet();
        resource.release();
        // END CRITICAL SECTION
    }

    private void doWrite(String data) throws IOException {
        try (FileOutputStream is = new FileOutputStream(file)) {
            is.write(data.getBytes());
        }
    }
}