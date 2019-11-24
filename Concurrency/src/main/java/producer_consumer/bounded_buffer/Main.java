package producer_consumer.bounded_buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    private static final int THREAD_COUNT = 11;
    private static final int PRODUCER_COUNT = 5;
    private static final int CONSUMER_COUNT = 6;

    private final BoundedBuffer buffer = new BoundedBufferImpl();
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {
        new Main().execute();
    }

    private void execute() {
        List<Future<Integer>> producerCounts = startProducers();
        List<Future<Integer>> consumerCounts = startConsumers();

        scheduleShutdown();

        reportCount("total messages sent: ", producerCounts);
        reportCount("total messages received: ", consumerCounts);
    }

    private List<Future<Integer>> startProducers() {
        List<Future<Integer>> producerCounts = new ArrayList<>();
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            producerCounts.add(executor.submit(new Producer(i, buffer)));
        }
        return producerCounts;
    }

    private List<Future<Integer>> startConsumers() {
        List<Future<Integer>> consumerCounts = new ArrayList<>();
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumerCounts.add(executor.submit(new Consumer(i, buffer)));
        }
        return consumerCounts;
    }

    private void scheduleShutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
            executor.shutdownNow();
        }).start();
    }

    private void reportCount(String s, List<Future<Integer>> counts) {
        int messagesSent = 0;
        for (Future<Integer> count : counts) {
            try {
                messagesSent += count.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
        System.out.println(s + messagesSent);
    }

    static class Producer implements Callable<Integer> {
        private final int id;
        private final BoundedBuffer buffer;
        private int messageCount;

        Producer(int id, BoundedBuffer buffer) {
            this.id = id;
            this.buffer = buffer;
        }

        @Override
        public Integer call() {
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
                    return messageCount;
                }
            }
        }
    }

    static class Consumer implements Callable<Integer> {
        private final int id;
        private final BoundedBuffer buffer;
        private int messageCount;

        Consumer(int id, BoundedBuffer buffer) {
            this.id = id;
            this.buffer = buffer;
        }

        @Override
        public Integer call() {
            int sleepInterval = ThreadLocalRandom.current().nextInt(100);
            while (true) {
                try {
                    String message = (String) buffer.take();
                    messageCount++;
                    System.out.format("[%d] message %d: %s%n", id, messageCount, message);
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException ignored) {
                    System.out.format("[%d] received %d messages%n", id, messageCount);
                    return messageCount;
                }
            }
        }
    }
}
