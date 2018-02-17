package ee.joonasvali.stamps.painting;

import ee.joonasvali.stamps.Projection;
import ee.joonasvali.stamps.ui.ProgressCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ProjectionRenderer {
  private static final int NUMBER_OF_PROJECTIONS_TO_PREPARE = 10;
  private static Logger log = LoggerFactory.getLogger(ProjectionRenderer.class);
  private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
  private static ExecutorService multiThreadExecutor = Executors.newFixedThreadPool(AVAILABLE_PROCESSORS);
  private ArrayList<Future> futures;
  private final ArrayBlockingQueue<Projection> projections = new ArrayBlockingQueue<>(NUMBER_OF_PROJECTIONS_TO_PREPARE);
  private final AtomicInteger remainingInQueue = new AtomicInteger();

  public void start(InterruptibleSupplier<Projection> projectionSupplier, int projectionCount, ProgressCounter counter) {
    remainingInQueue.set(projectionCount);

    Runnable internalRunnable = () -> {
      try {
        projections.put(projectionSupplier.get());
        remainingInQueue.decrementAndGet();
      } catch (InterruptedException e) {
        log.info("Projection interrupted.");
      } finally {
        counter.increase();
      }
    };

    futures = new ArrayList<>(projectionCount);
    for (int i = 0; i < projectionCount; i++) {
      futures.add(multiThreadExecutor.submit(internalRunnable));
    }
  }

  public void cancel() {
    for(Future future : futures) {
      if(!future.isDone() && !future.isCancelled()) {
        future.cancel(true);
      }
    }
    log.info("Rendering cancelled.");
    futures.clear();
  }

  public boolean hasNext() {
    return remainingInQueue.get() > 0 || projections.size() > 0;
  }

  public Projection next() throws InterruptedException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return projections.take();
  }
}
