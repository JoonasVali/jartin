package ee.joonasvali.stamps.painting;

public interface InterruptibleRunnable {
  void run() throws InterruptedException;
}
