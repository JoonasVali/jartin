package ee.joonasvali.stamps.painting;

public interface InterruptibleSupplier<T> {
  T get() throws InterruptedException;
}
