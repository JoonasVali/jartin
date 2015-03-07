package ee.joonasvali.stamps.ui;

import ee.joonasvali.stamps.Painting;
import ee.joonasvali.stamps.ProjectionGenerator;
import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.ColorUtil;
import ee.joonasvali.stamps.color.GradientColorModel;
import ee.joonasvali.stamps.color.Pallette;
import ee.joonasvali.stamps.color.PlainColorModel;
import ee.joonasvali.stamps.properties.AppProperties;
import ee.joonasvali.stamps.query.BinaryFormula;
import ee.joonasvali.stamps.query.BinaryFormulaGenerator;
import ee.joonasvali.stamps.query.BinaryQuery;
import ee.joonasvali.stamps.query.BinaryValue;
import ee.joonasvali.stamps.query.Query;
import ee.joonasvali.stamps.query.RandomQuery;
import ee.joonasvali.stamps.query.ReversingCompoundBinaryFormula;
import ee.joonasvali.stamps.query.XYFormulaQuery;
import ee.joonasvali.stamps.stamp.CompositeStamps;
import ee.joonasvali.stamps.stamp.GroupedStamps;
import ee.joonasvali.stamps.stamp.RandomIntersectionComposerStrategy;
import ee.joonasvali.stamps.stamp.RandomMergeComposerStrategy;
import ee.joonasvali.stamps.stamp.Stamp;
import ee.joonasvali.stamps.stamp.StampProvider;
import ee.joonasvali.stamps.stamp.Stamps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Joonas Vali
 */
public final class PaintingController {
  private static Logger log = LoggerFactory.getLogger(PaintingController.class);
  private static final double CHANCE_OF_GRADIENT_COLOR = 0.7;
  private static ExecutorService multiThreadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  private final Preferences prefs = new Preferences();
  private final BinaryFormulaGenerator colorFormulaGenerator;
  private final BinaryFormulaGenerator stampFormulaGenerator;
  private final BinaryFormulaGenerator colorModelFormulaGenerator;

  private volatile boolean retainColors = false;
  private volatile boolean retainStamps = false;
  private volatile boolean retainSpine = false;


  private GroupedStamps stampPool = new GroupedStamps(AppProperties.getInstance().getStampsDir());

  private volatile Pallette pallette;
  private volatile StampProvider stamps;

  private volatile Query<Stamp> stampQuery;
  private volatile Query<ColorModel> colorModelQuery;
  private volatile Query<Color> colorQuery;

  public PaintingController(BinaryFormulaGenerator colorModelFormulaGenerator, BinaryFormulaGenerator stampFormulaGenerator, BinaryFormulaGenerator colorFormulaGenerator) {
    this.colorModelFormulaGenerator = colorModelFormulaGenerator;
    this.stampFormulaGenerator = stampFormulaGenerator;
    this.colorFormulaGenerator = colorFormulaGenerator;
  }

  public void setRetainColors(boolean retainColors) {
    this.retainColors = retainColors;
  }

  public void setRetainStamps(boolean retainStamps) {
    this.retainStamps = retainStamps;
  }

  public void setRetainSpine(boolean retainSpine) {
    this.retainSpine = retainSpine;
  }

  public void clearCaches() {
    log.info("Clearing Caches");
    Stamp.clearCache();
    stampPool.clearCaches();
  }

  /**
   * @return Image or null if cancelled
   */
  public synchronized BufferedImage generateImage(ProgressListener listener) {
    long startTime = System.currentTimeMillis();
    log.info("Starting generating a new image");
    int x = prefs.getWidth();
    int y = prefs.getHeight();

    stamps = createStamps();

    if (pallette == null || !retainColors) {
      pallette = new Pallette(generateColorModels(new Random()));
    } else {
      log.debug("Skip generating color models");
    }

    ProjectionGenerator gen = new ProjectionGenerator(x, y, stamps, pallette);

    boolean showSpine = prefs.isSpineMode();

    int projections = 0;
    if (!showSpine) {
      projections = (x * y / prefs.getStampCountDemultiplier());
      log.info("Number of projections: " + projections);
    }

    Painting painting = new Painting(x, y, pallette, projections);


    if (stampQuery == null || colorModelQuery == null || colorQuery == null || !retainSpine) {
      stampQuery = generateXYFormulaQuery(stampFormulaGenerator);
      colorModelQuery = generateXYFormulaQuery(colorModelFormulaGenerator);
      colorQuery = generateXYFormulaQuery(colorFormulaGenerator);
    }


    ProgressCounter counter = new ProgressCounter(listener, projections);
    if (!showSpine) {
      try {
        log.info("Start painting.");
        painting.startPainting(counter);
        addProjections(gen, painting, projections, Runtime.getRuntime().availableProcessors());
      }
      finally {
        try {
          painting.stopPainting();
          log.info("Stop painting.");
        } catch (InterruptedException e) {
          log.info("Painting cancelled.");
          counter.clear();
          return null;
        }
      }

    } else {
      log.warn("SPINE MODE!");
      return paintLines(colorModelQuery);
    }

    long endTime = System.currentTimeMillis();
    log.info("Generating new image completed. Total time: " + (endTime - startTime) + " ms");

    try {
      return painting.getImage();
    } catch (InterruptedException e) {
      log.info("Painting cancelled.");
      counter.clear();
      Thread.currentThread().interrupt();
      return null;
    }
  }

  private StampProvider createStamps() {
    if (stamps == null || !retainStamps) {
      log.info("Generating stamps");
      Stamps loadedStamps = stampPool.getStamps(prefs.getStampGroupsCount(), prefs.getStampsPerGroup(), RandomQuery.create(), RandomQuery.create(), false);
      CompositeStamps compositeStamps = new CompositeStamps(loadedStamps, new RandomIntersectionComposerStrategy((int) (Math.random() * 10)));
      compositeStamps = new CompositeStamps(compositeStamps, new RandomMergeComposerStrategy((int) (Math.random() * 10)));
      return compositeStamps;
    } else {
      log.debug("Skip generating stamps");
      return stamps;
    }
  }

  private void addProjections(ProjectionGenerator gen, Painting painting, int projections, int processors) {
    if (processors <= 1) {
      // SINGLETHREADED LOGIC
      for (int i = 0; i < projections; i++) {
        painting.addProjection(gen.generate(stampQuery, colorModelQuery, colorQuery));
      }
    } else {
      // MULTITHREADED LOGIC
      CountDownLatch latch = new CountDownLatch(projections);

      Runnable runnable = () -> {
        try {
          painting.addProjection(gen.generate(stampQuery, colorModelQuery, colorQuery));
        } finally {
          latch.countDown();
        }
      };

      ArrayList<Future> futures = new ArrayList<>(projections);
      for (int i = 0; i < projections; i++) {
        futures.add(multiThreadExecutor.submit(runnable));
      }

      try {
        latch.await();
      } catch (InterruptedException e) {
        for(Future future : futures) {
          if(!future.isDone() && !future.isCancelled()) {
            future.cancel(true);
          }
        }
        painting.cancel();
        Thread.currentThread().interrupt();
      }
    }
  }


  private <T> Query<T> generateXYFormulaQuery(BinaryFormulaGenerator generator) {
    ReversingCompoundBinaryFormula formula = new ReversingCompoundBinaryFormula(generator.generate(prefs));
    return new XYFormulaQuery(new RandomQuery<T>(), new BinaryQuery<T>(Math.random()), formula);
  }

  private java.util.List<ColorModel> generateColorModels(Random random) {
    log.info("Generating color models");
    int colors = prefs.getNumberOfColors();
    java.util.List<ColorModel> colorModels = new ArrayList<>(colors);

    for (int i = 0; i < colors; i++) {
      if (Math.random() < CHANCE_OF_GRADIENT_COLOR) {
        colorModels.add(new GradientColorModel(ColorUtil.getRandomColor(random), ColorUtil.getRandomColor(random), prefs.getHeight(), prefs.getWidth()));
      } else {
        colorModels.add(new PlainColorModel(ColorUtil.getRandomColor(random)));
      }
    }
    return colorModels;
  }

  public Preferences getPrefs() {
    return prefs;
  }

  private BufferedImage paintLines(Query<?> q) {
    BufferedImage image = new BufferedImage(getPrefs().getWidth(), getPrefs().getHeight(), BufferedImage.TYPE_INT_RGB);
    if (!(q instanceof XYFormulaQuery)) {
      log.error("Invalid query for paintLines");
      System.exit(-1);
    }

    BinaryFormula formula = ((XYFormulaQuery) q).getFormula();

    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        Color color = formula.get(i, j).equals(BinaryValue.ONE) ? Color.GRAY : Color.DARK_GRAY;
        image.setRGB(i, j, color.getRGB());
      }
    }

    return image;

  }

}
