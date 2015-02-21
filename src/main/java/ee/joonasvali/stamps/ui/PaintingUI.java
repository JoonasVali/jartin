package ee.joonasvali.stamps.ui;

import ee.joonasvali.stamps.Painting;
import ee.joonasvali.stamps.ProjectionGenerator;
import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.ColorUtil;
import ee.joonasvali.stamps.color.GradientColorModel;
import ee.joonasvali.stamps.color.Pallette;
import ee.joonasvali.stamps.color.PlainColorModel;
import ee.joonasvali.stamps.meta.Metadata;
import ee.joonasvali.stamps.properties.AppProperties;
import ee.joonasvali.stamps.query.BinaryFormula;
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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Joonas Vali
 * TODO refactor non UI logic out of here
 */
public class PaintingUI extends JPanel {

  public static Logger log = LoggerFactory.getLogger(PaintingUI.class);
  public static final double CHANCE_OF_GRADIENT_COLOR = 0.7;

  private ExecutorService generalGeneratorExecutor = Executors.newSingleThreadExecutor();
  private static ExecutorService multiThreadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  private boolean retainColors = false;
  private boolean retainStamps = false;
  private boolean retainSpine = false;
  private Preferences prefs = new Preferences();
  private ProgressListener progressListener;

  private Query<Stamp> stampQuery = getStampQuery();
  private Query<ColorModel> colorModelQuery = getColorModelQuery();
  private Query<Color> colorQuery = RandomQuery.create();

  private volatile Pallette pallette;
  private volatile StampProvider stamps;

  private BufferedImage lastImage;

  static GroupedStamps stampPool = new GroupedStamps(AppProperties.getInstance().getStampsDir());

  public PaintingUI() {
    initEmpty();
    this.add(new JLabel(new ImageIcon(lastImage)));
  }

  private void initEmpty() {
    log.debug("Start initializing PaintingUI");
    lastImage = new BufferedImage(prefs.getWidth(), prefs.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D) lastImage.getGraphics();
    g.setColor(Color.LIGHT_GRAY);
    int max = (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024));
    String rating = "OK";
    if (max < 800) {
      rating = "low";
    } else if (max < 1000) {
      rating = "could use more";
    }

    int i = 35;
    int processors = Runtime.getRuntime().availableProcessors();
    g.drawString(Metadata.VERSION, 50, i);
    g.drawString("Image size set to " + prefs.getWidth() + " : " + prefs.getHeight(), 50, i + 15);
    g.drawString("Total memory available to Java VM: " + max + " MB " + "(" + rating + ")", 50, i + 30);
    g.drawString("Number of processors available to Java VM: " + processors, 50, i + 45);
    if(AppProperties.getInstance().isLazyLoading()) {
      g.drawString("Using lazy loading for stamps (Slower but conserves memory).", 50, i + 60);
      i += 15;
    }
    g.drawString("Press \"Generate\" to generate your first image.", 50, i + 60);
    log.debug("Stop initializing PaintingUI");
  }

  public BufferedImage getLastImage() {
    return lastImage;
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

  public void onReinit(Runnable after) {
    assert SwingUtilities.isEventDispatchThread();
    generalGeneratorExecutor.execute(() -> {
      clearCaches();
      init();
      PaintingUI.this.removeAll();
      PaintingUI.this.add(new JLabel(new ImageIcon(lastImage)));
      after.run();
    });
  }

  private void clearCaches() {
    log.info("Clearing Caches");
    Stamp.clearCache();
    stampPool.clearCaches();
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

  private synchronized void init() {
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

    int projections = (x * y / prefs.getStampCountDemultiplier());
    log.info("Number of projections: " + projections);
    Painting painting = new Painting(x, y, pallette, projections);


    boolean showSpine = prefs.isSpineMode();
    if (stampQuery == null || colorModelQuery == null || colorQuery == null || !retainSpine) {
      stampQuery = getStampQuery();
      colorModelQuery = getColorModelQuery();
      colorQuery = getColorQuery();
    }


    ProgressCounter counter = new ProgressCounter(progressListener, projections);
    if (!showSpine) {
      try {
        log.info("Start painting.");
        painting.startPainting(counter);
        addProjections(gen, painting, projections, Runtime.getRuntime().availableProcessors());
      } finally {
        painting.stopPainting();
        log.info("Stop painting.");
      }

    } else {
      log.warn("SPINE MODE!");
      paintLines(colorModelQuery, painting);
    }

    try {
      lastImage = painting.getImage();
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      System.exit(-1);
    }
    long endTime = System.currentTimeMillis();
    log.info("Generating new image completed. Total time: " + (endTime - startTime) + " ms");
  }

  private void addProjections(ProjectionGenerator gen, Painting painting, int projections, int processors) {
    if (processors <= 1) {
      // SINGLETHREADED LOGIC
      for (int i = 0; i < projections; i++) {
        painting.addProjection(gen.generate(stampQuery, colorModelQuery, colorQuery));
      }
    } else {
      // MULTITHREADED LOGIC
      CountDownLatch latch = new CountDownLatch(processors);
      int projectionsPerCore = projections / processors;
      int remaining = projections % processors;
      Runnable runnable = () -> {
        try {
          for (int i = 0; i < projectionsPerCore; i++) {
            painting.addProjection(gen.generate(stampQuery, colorModelQuery, colorQuery));
          }
        } finally {
          latch.countDown();
        }
      };
      for (int i = 0; i < processors; i++) {
        multiThreadExecutor.execute(runnable);
      }

      if (remaining > 0) {
        // Add remaining projections
        addProjections(gen, painting, remaining, 1);
      }

      try {
        latch.await();
      } catch (InterruptedException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private void paintLines(Query<?> q, Painting painting) {
    if (!(q instanceof XYFormulaQuery)) {
      return;
    }
    BinaryFormula formula = ((XYFormulaQuery) q).getFormula();
    BufferedImage image = null;
    try {
      image = painting.getImage();
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      System.exit(-1);
    }

    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        Color color = formula.get(i, j).equals(BinaryValue.ONE) ? Color.GRAY : Color.DARK_GRAY;
        image.setRGB(i, j, color.getRGB());
      }
    }
  }

  private Query<ColorModel> getColorModelQuery() {
    return new XYFormulaQuery(new RandomQuery(), new BinaryQuery(Math.random()), new ReversingCompoundBinaryFormula(generateFormula(), generateFormula()));
  }

  private Query<Stamp> getStampQuery() {
    return new XYFormulaQuery(new RandomQuery(), new BinaryQuery(Math.random()), new ReversingCompoundBinaryFormula(generateFormula(), generateFormula()));
  }

  private Query<Color> getColorQuery() {
    return new XYFormulaQuery(new RandomQuery(), new BinaryQuery(Math.random()), new ReversingCompoundBinaryFormula(generateFormula(), generateFormula()));
  }

  private BinaryFormula generateFormula() {
    int waves = (int) (Math.random() * 3 + 1);
    BinaryFormula[] formulas = new BinaryFormula[waves];

    for (int i = 0; i < waves; i++) {
      double wavelength = Math.random() * (prefs.getWidth() / 400) + (prefs.getWidth() / 800);
      double offset = Math.random() * Math.PI;
      int movement = prefs.getHeight() / 2;
      int n = (int) ((Math.random() * (prefs.getHeight() - movement)) + movement) - prefs.getHeight() / 4;

      int slope = (int) (Math.random() * 200);

      formulas[i] =
          (x, y) -> {
            double s = Math.sin(Math.toRadians(x / wavelength) + offset) * slope + n;
            return y > s ? BinaryValue.ONE : BinaryValue.ZERO;
          };

    }
    if (waves > 1) {
      return new ReversingCompoundBinaryFormula(formulas);
    } else {
      return formulas[0];
    }
  }

  private List<ColorModel> generateColorModels(Random random) {
    log.info("Generating color models");
    int colors = prefs.getNumberOfColors();
    List<ColorModel> colorModels = new ArrayList<>(colors);

    for (int i = 0; i < colors; i++) {
      if (Math.random() < CHANCE_OF_GRADIENT_COLOR) {
        colorModels.add(new GradientColorModel(ColorUtil.getRandomColor(random), ColorUtil.getRandomColor(random), getHeight(), getWidth()));
      } else {
        colorModels.add(new PlainColorModel(ColorUtil.getRandomColor(random)));
      }
    }
    return colorModels;
  }

  public Preferences getPrefs() {
    return prefs;
  }


  public void setProgressListener(ProgressListener listener) {
    this.progressListener = listener;
  }
}
