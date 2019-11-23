package producer_consumer.bounded_buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        BoundedBuffer buffer = new BoundedBufferImpl();
        ExecutorService executor = Executors.newFixedThreadPool(11);

        List<Future<Integer>> producerCounts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            producerCounts.add(executor.submit(new Producer(i, buffer)));
        }
        List<Future<Integer>> consumerCounts = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            consumerCounts.add(executor.submit(new Consumer(i, buffer)));
        }

        scheduleShutdown(executor);

        int messagesSent = 0;
        for (Future<Integer> count : producerCounts) {
            try {
                messagesSent += count.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
        System.out.println("total messages sent: " + messagesSent);

        int messagesReceived = 0;
        for (Future<Integer> count : consumerCounts) {
            try {
                messagesReceived += count.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
        System.out.println("total messages received: " + messagesReceived);
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
