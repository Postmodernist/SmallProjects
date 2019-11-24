package reader_writer.reader_preference;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final String fileName = "tmp";
    private static final String[] strings = {
            "Body my house",
            "my horse my hound",
            "what will I do",
            "when you are fallen",
            "Where will I sleep",
            "How will I ride",
            "What will I hunt",
            "Where can I go",
            "without my mount",
            "all eager and quick",
            "How will I know",
            "in thicket ahead",
            "is danger or treasure",
            "when Body my good",
            "bright dog is dead",
            "How will it be",
            "to lie in the sky",
            "without roof or door",
            "and wind for an eye",
            "With cloud for shift",
            "how will I hide?"
    };
    private static final int WRITER_COUNT = 5;
    private static final int READER_COUNT = 5;

    public static void main(String[] args) throws IOException {
        new Main().execute();
    }

    private void execute() throws IOException {
        final File file = getFile();
        final Semaphore resource = new Semaphore(1);
        final ReentrantLock readMutex = new ReentrantLock();

        final ExecutorService executor =
                Executors.newFixedThreadPool(WRITER_COUNT + READER_COUNT);

        for (int i = 0; i < WRITER_COUNT; i++) {
            final int id = i;
            executor.execute(() -> writeLoop(new Writer(file, resource), id));
        }

        for (int i = 0; i < READER_COUNT; i++) {
            final int id = i;
            executor.execute(() -> readLoop(new Reader(file, resource, readMutex), id));
        }

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        } catch (InterruptedException ignored) {
        }
        executor.shutdownNow();

        System.out.format("total writes: %d%n", Writer.writeCount.get());
        System.out.format("total reads: %d%n", Reader.readCount.get());
    }

    private File getFile() throws IOException {
        File file = new File(fileName);
        if (file.exists() && !file.delete()) file = null;
        if (file != null && !file.createNewFile()) file = null;
        if (file == null) {
            System.err.println("Failed to open file: " + fileName);
            System.exit(0);
        }
        return file;
    }

    private void writeLoop(Writer writer, int id) {
        int i = 0;
        while (true) {
            try {
                writer.write(strings[i++ % strings.length]);
            } catch (InterruptedException e) {
                System.out.format("[%d] writer shut down%n", id);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readLoop(Reader reader, int id) {
        while (true) {
            try {
                reader.read();
            } catch (InterruptedException e) {
                System.out.format("[%d] reader shut down%n", id);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
