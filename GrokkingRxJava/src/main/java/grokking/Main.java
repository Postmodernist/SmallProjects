package grokking;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

  private static void example1() {
    // Transform stream of lists to stream of individual items
    List<String> stringList = Arrays.asList("Foo", "Bar", "Baz", "Qux");

    Disposable d = Flowable.just(stringList)
        .flatMap(Flowable::fromIterable)
        .subscribe(System.out::println);
    d.dispose();
  }

  private static void example2() {
    // Observable
    Disposable d = Observable.just("Hello world!")
        .delay(1, TimeUnit.SECONDS)
        .subscribeWith(new DisposableObserver<String>() {
          @Override
          public void onStart() {
            System.out.println("Start!");
          }

          @Override
          public void onNext(String t) {
            System.out.println(t);
          }

          @Override
          public void onError(Throwable t) {
            t.printStackTrace();
          }

          @Override
          public void onComplete() {
            System.out.println("Done!");
          }
        });

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // the sequence now can be cancelled via dispose()
    d.dispose();
  }

  private static void example3() {
    // Assembly time
    Flowable<Integer> flow = Flowable.range(1, 5)
        .map(v -> v * v)
        .filter(v -> v % 3 == 0);
    // Subscription time
    Disposable d = flow.subscribe(System.out::println);
    d.dispose();
  }

  private static void example4() {
    // Runtime
    Disposable d = Observable.create(emitter -> {
      while (!emitter.isDisposed()) {
        long time = System.currentTimeMillis();
        emitter.onNext(time);
        if (time % 2 != 0) {
          emitter.onError(new IllegalStateException("Odd millisecond!"));
          break;
        }
      }
    }).subscribe(System.out::println, Throwable::printStackTrace);
    d.dispose();
  }

  private static void example5() {
    // Run on background, consume on main thread
    Disposable d = Flowable.fromCallable(() -> {
      Thread.sleep(1000);  // imitate expensive computation
      return "Done";
    })
        .subscribeOn(Schedulers.io())  // run on background
        .observeOn(Schedulers.single())  // show on foreground
        .subscribe(System.out::println, Throwable::printStackTrace);

    try {
      Thread.sleep(2000);  // <--- wait for the flow to finish
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    d.dispose();
  }

  private static void example6() {
    // Concurrency within a flow
    Flowable.range(1, 10)
        .observeOn(Schedulers.computation())
        .map(v -> v * v)
        .blockingSubscribe(System.out::println);
  }

  private static void example7() {
    // Parallel processing
    Flowable.range(1, 10)
        .flatMap(v ->
            Flowable.just(v)
                .subscribeOn(Schedulers.computation())
                .map(w -> w * w))
        .blockingSubscribe(System.out::println);
  }

  private static void example8() {
    // Parallel processing (alternative)
    Flowable.range(1, 10)
        .parallel()
        .runOn(Schedulers.computation())
        .map(v -> v * v)
        .sequential()
        .blockingSubscribe(System.out::println);
  }

  private static void example9() {
    // Deferred-dependent
    AtomicInteger count = new AtomicInteger();

    Disposable d = Observable.range(1, 10)
        .doOnNext(ignored -> count.incrementAndGet())
        .ignoreElements()
        .andThen(Single.fromCallable(count::get))
        .subscribe(System.out::println);
    d.dispose();
  }

  public static void main(String[] args) {
//    example1();
//    example2();
//    example3();
//    example4();
//    example5();
//    example6();
//    example7();
//    example8();
    example9();
  }
}
