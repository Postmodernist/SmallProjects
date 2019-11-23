package producer_consumer.bounded_buffer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {
        BoundedBuffer buffer = new BoundedBufferImpl();
        int processorsCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processorsCount);

        for (int i = 0; i < processorsCount - 1; i++) {
            Producer producer = new Producer(i, buffer);
            executor.submit(producer);
        }
        Runnable consumer = new Thread(new Consumer(buffer));
        executor.submit(consumer);

        scheduleShutdown(executor);
    }

    private static void scheduleShutdown(ExecutorService executor) {
        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
            executor.shutdownNow();
        }).start();
    }

    static class Producer implements Runnable {
        private final int id;
        private final BoundedBuffer buffer;
        private int messageCount;

        Producer(int id, BoundedBuffer buffer) {
            this.id = id;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (true) {
                int payload = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
                String message = id + "_" + payload;
                int sleepInterval = ThreadLocalRandom.current().nextInt(100);
                try {
                    buffer.put(message);
                    messageCount++;
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException ignored) {
                    System.out.format("[%d] sent %d messages%n", id, messageCount);
                    return;
                }
            }
        }
    }

    static class Consumer implements Runnable {
        private final BoundedBuffer buffer;
        private int messageCount;

        Consumer(BoundedBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            int sleepInterval = ThreadLocalRandom.current().nextInt(100);
            while (true) {
                try {
                    String message = (String) buffer.take();
                    messageCount++;
                    System.out.format("message %d: %s%n", messageCount, message);
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException ignored) {
                    System.out.format("received %d messages%n", messageCount);
                    return;
                }
            }
        }
    }
}
