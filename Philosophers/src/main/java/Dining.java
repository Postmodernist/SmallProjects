import org.nustaq.kontraktor.Actor;
import org.nustaq.kontraktor.Actors;
import org.nustaq.kontraktor.IPromise;
import org.nustaq.kontraktor.Promise;
import org.nustaq.kontraktor.remoting.tcp.TCPConnectable;
import org.nustaq.kontraktor.remoting.tcp.TCPServerConnector;
import org.nustaq.kontraktor.util.Hoarde;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by ruedi on 24.10.14.
 * <p>
 * (Distributed) Solution of the Dining Philosopher Problem using actors. (no attempt is made on being fair)
 */
public class Dining {

    public static void main(String[] arg) {
        String mode = arg.length == 0 ? "default" : arg[0];
        switch (mode) {
            case "server":
                runServer();
                break;
            case "client":
                runClient();
                break;
            default:
                // run them in process
                runPhilosophers(Actors.AsActor(Table.class));
        }
    }

    private static void runServer() {
        TCPServerConnector.Publish(Actors.AsActor(Table.class), 6789, null);
    }

    private static void runClient() {
        new TCPConnectable(Table.class, "localhost", 6789)
                .connect()
                .then((table, error) -> {
                    if (table != null) { // connection failure
                        runPhilosophers((Table) table);
                    } else {
                        System.out.println("error:" + error);
                    }
                });
    }

    private static void runPhilosophers(Table coord) {
        String[] names = {"A", "B", "C", "D", "E"};
        Hoarde<Philosopher> philosophers =
                new Hoarde<>(5, Philosopher.class)
                        .each((phi, i) -> phi.start(names[i], i, coord));
        startReportingThread(philosophers);
    }

    private static void startReportingThread(Hoarde<Philosopher> philosophers) {
        // start a thread reporting state each second
        new Thread(() -> {
            while (true) {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                IPromise<Philosopher>[] philosopherStates =
                        philosophers.map((phil, index) -> phil.getState());
                Actors.all(philosopherStates).then((futs, e) -> {
                    for (IPromise<Philosopher> fut : futs) {
                        System.out.print(fut.get() + ", ");
                    }
                    System.out.println();
                });
            }
        }).start();
    }

    @SuppressWarnings("WeakerAccess")
    public static class Table extends Actor<Table> {
        private static final int FORKS_COUNT = 5;

        List<List<Promise>> forks = new ArrayList<>(FORKS_COUNT);

        public Table() {
            for (int i = 0; i < FORKS_COUNT; i++) {
                forks.add(new ArrayList<>());
            }
        }

        public IPromise<String> getFork(int num) {
            num %= FORKS_COUNT;
            Promise<String> res =
                    forks.get(num).isEmpty() ? new Promise<>("void") : new Promise<>();
            forks.get(num).add(res);
            return res;
        }

        public void returnFork(int num) {
            num %= FORKS_COUNT;
            forks.get(num).remove(0);
            if (!forks.get(num).isEmpty())
                forks.get(num).get(0).complete();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class Philosopher extends Actor<Philosopher> {
        String name;
        int nr;
        Table table;
        String state;
        int eatCount;

        public void start(String name, int nr, Table table) {
            this.name = name;
            this.nr = nr;
            this.table = table;
            live();
        }

        public void live() {
            state = "Think";
            long thinkTime = randomTimeMS();
            delayed(thinkTime, () -> {
                state = "Hungry";
                // avoid deadlock:
                // even numbered philosophers take left then right fork,
                // odd numbered vice versa
                int firstFork = nr + (nr & 1);
                int secondFork = nr + (1 - (nr & 1));
                table.getFork(firstFork).then((r, e) ->
                        table.getFork(secondFork).then((r1, e1) -> {
                            state = "Eat";
                            long eatTime = randomTimeMS();
                            delayed(eatTime, () -> {
                                eatCount++;
                                table.returnFork(firstFork);
                                table.returnFork(secondFork);
                                self().live();
                            });
                        })
                );
            });
        }

        public IPromise<String> getState() {
            return new Promise<>(name + " " + state + " eaten:" + eatCount);
        }

        private long randomTimeMS() {
            return (long) (100 * Math.random() + 1);
        }
    }
}