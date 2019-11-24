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
        resource.acquire();
        // BEGIN CRITICAL SECTION
        doWrite(data);
        writeCount.incrementAndGet();
        // END CRITICAL SECTION
        resource.release();
    }

    private void doWrite(String data) throws IOException {
        try (FileOutputStream is = new FileOutputStream(file)) {
            is.write(data.getBytes());
        }
    }
}
