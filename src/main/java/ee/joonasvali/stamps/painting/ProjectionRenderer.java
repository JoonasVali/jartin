package ee.joonasvali.stamps.painting;

import ee.joonasvali.stamps.Projection;
import ee.joonasvali.stamps.ui.ProgressCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class ProjectionRenderer {
  private static Logger log = LoggerFactory.getLogger(ProjectionRenderer.class);
  private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
  private static ExecutorService multiThreadExecutor = Executors.newFixedThreadPool(AVAILABLE_PROCESSORS);

  private final int processors;

  public ProjectionRenderer() {
    this(AVAILABLE_PROCESSORS);
  }

  public ProjectionRenderer(int threads) {
    this.processors = threads;
  }

  public List<Projection> render(Supplier<Projection> projectionSupplier, int projections, ProgressCounter counter) throws InterruptedException {
    List<Projection> projectionList = Collections.synchronizedList(new ArrayList<>());
    if (processors <= 1) {
      // SINGLETHREADED LOGIC
      for (int i = 0; i < projections; i++) {
        if (Thread.currentThread().isInterrupted()) {
          log.info("Rendering cancelled.");
          throw new InterruptedException();
        }
        projectionList.add(projectionSupplier.get());
        counter.increase();
      }
    } else {
      // MULTITHREADED LOGIC
      CountDownLatch latch = new CountDownLatch(projections);

      Runnable internalRunnable = () -> {
        try {
          projectionList.add(projectionSupplier.get());
        } finally {
          counter.increase();
          latch.countDown();
        }
      };

      ArrayList<Future> futures = new ArrayList<>(projections);
      for (int i = 0; i < projections; i++) {
        futures.add(multiThreadExecutor.submit(internalRunnable));
      }

      try {
        latch.await();
      } catch (InterruptedException e) {
        for(Future future : futures) {
          if(!future.isDone() && !future.isCancelled()) {
            future.cancel(true);
          }
        }
        log.info("Rendering cancelled.");
        futures.clear();
        Thread.currentThread().interrupt();
      }
    }

    return projectionList;
  }

}
