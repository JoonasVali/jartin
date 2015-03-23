package ee.joonasvali.stamps.ui;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joonas Vali
 */
public class ProgressCounter {
  private AtomicInteger count = new AtomicInteger();
  private ProgressListener listener;
  private int totalProjections;

  public ProgressCounter(ProgressListener listener, int totalProjections) {
    this.listener = listener;
    this.totalProjections = totalProjections;
  }

  public void increase() {
    int val = (int)((double)count.incrementAndGet() / (double)totalProjections * 100);
    listener.setValue(val);
  }

  public void clear() {
    count.set(0);
    listener.setValue(0);
  }

  public void setValue(String message) {
    listener.setValue(message);
  }
}
