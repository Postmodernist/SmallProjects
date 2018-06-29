package grokking;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class RecursiveDep {

  private static Observable<Integer> getSeq() {
    return Observable.concat(Observable.range(0, 3), Observable.defer(RecursiveDep::getSeq)
        .map(integer -> integer + 3));
  }

  public static void main(String[] args) {
    Disposable disposable = getSeq().take(10).forEach(System.out::println);
    disposable.dispose();
  }
}
