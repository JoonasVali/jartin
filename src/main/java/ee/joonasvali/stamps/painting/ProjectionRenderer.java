package ee.joonasvali.stamps.painting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

  public void render(Painting painting, InterruptibleRunnable renderFunction, int projections) {
    if (processors <= 1) {
      // SINGLETHREADED LOGIC
      for (int i = 0; i < projections; i++) {
        try {
          renderFunction.run();
        } catch (InterruptedException e) {
          log.info("Rendering cancelled.");
          painting.cancel();
          Thread.currentThread().interrupt();
        }
      }
    } else {
      // MULTITHREADED LOGIC
      CountDownLatch latch = new CountDownLatch(projections);

      Runnable internalRunnable = () -> {
        try {
          renderFunction.run();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (Exception e) {
          log.error("Projection adding failed", e);
        } finally {
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
        painting.cancel();
        futures.clear();
        Thread.currentThread().interrupt();
      }
    }
  }

}
