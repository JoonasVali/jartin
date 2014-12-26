package ee.joonasvali.stamps.ui;

import ee.joonasvali.stamps.CompositeStamps;
import ee.joonasvali.stamps.GroupedStamps;
import ee.joonasvali.stamps.Painting;
import ee.joonasvali.stamps.ProjectionGenerator;
import ee.joonasvali.stamps.RandomComposerStrategy;
import ee.joonasvali.stamps.Stamp;
import ee.joonasvali.stamps.Stamps;
import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.ColorUtil;
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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joonas Vali
 * TODO refactor non UI logic out of here
 */
public class PaintingUI extends JPanel {

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

  private Pallette pallette;
  private Stamps stamps;

  private BufferedImage lastImage;

  static GroupedStamps stampPool = new GroupedStamps(AppProperties.getInstance().getStampsDir());

  public PaintingUI() {
    initEmpty();
    this.add(new JLabel(new ImageIcon(lastImage)));
  }

  private void initEmpty() {
    lastImage = new BufferedImage(prefs.getWidth(), prefs.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D) lastImage.getGraphics();
    g.setColor(Color.LIGHT_GRAY);
    int max = (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024));
    System.out.println(Runtime.getRuntime().availableProcessors());
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
    g.drawString("Press \"Generate\" to generate your first image.", 50, i + 60);
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
    generalGeneratorExecutor.execute(() -> {
      clearCaches();
      init();
      PaintingUI.this.removeAll();
      PaintingUI.this.add(new JLabel(new ImageIcon(lastImage)));
      after.run();
    });
  }

  private void clearCaches() {
    Stamp.clearCache();
    stampPool.clearCaches();
  }

  private Stamps createStamps() {
    if (stamps == null || !retainStamps) {
      return stampPool.getStamps(prefs.getStampGroupsCount(), prefs.getStampsPerGroup(), RandomQuery.create(), RandomQuery.create(), false);
    } else {
      return stamps;
    }
  }

  private synchronized void init() {
    int x = prefs.getWidth();
    int y = prefs.getHeight();

    stamps = createStamps();
    CompositeStamps compositeStamps = new CompositeStamps(stamps, new RandomComposerStrategy(5));

    if (pallette == null || !retainColors) {
      pallette = new Pallette(generateColorModels(new Random()));
    }

    ProjectionGenerator gen = new ProjectionGenerator(x, y, compositeStamps, pallette);

    int projections = (x * y / prefs.getStampCountDemultiplier());
    Painting painting = new Painting(x, y, pallette, projections);


    boolean showSpine = prefs.isSpineMode();

    if (stampQuery == null || colorModelQuery == null || colorQuery == null || !retainSpine) {
      stampQuery = getStampQuery();
      colorModelQuery = getColorModelQuery();
      colorQuery = getColorQuery();
    }


    ProgressCounter counter = new ProgressCounter(progressListener, projections);
    if (!showSpine) {
      addProjections(gen, painting, projections, Runtime.getRuntime().availableProcessors(), counter);
    } else {
      paintLines(colorModelQuery, painting);
    }

    lastImage = painting.getImage();
  }

  private void addProjections(ProjectionGenerator gen, Painting painting, int projections, int processors, ProgressCounter counter) {
    if (processors <= 1) {
      // SINGLETHREADED LOGIC
      for (int i = 0; i < projections; i++) {
        painting.addProjection(gen.generate(stampQuery, colorModelQuery, colorQuery));
        counter.increase();
      }
    } else {
      // MULTITHREADED LOGIC
      CountDownLatch latch = new CountDownLatch(processors);
      int localProjections = projections / processors;
      int remaining = projections % processors;
      Runnable runnable = () -> {
        try {

          for (int i = 0; i < localProjections; i++) {
            painting.addProjection(gen.generate(stampQuery, colorModelQuery, colorQuery));
            counter.increase();
          }

        } finally {
          latch.countDown();
        }
      };
      for (int i = 0; i < processors; i++) {
        multiThreadExecutor.execute(runnable);
      }
      try {
        latch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (remaining > 0) {
        // Add remaining projections
        addProjections(gen, painting, remaining, 1, counter);
      }
    }
  }

  private void paintLines(Query<?> q, Painting painting) {
    if (!(q instanceof XYFormulaQuery)) {
      return;
    }
    BinaryFormula formula = ((XYFormulaQuery) q).getFormula();
    BufferedImage image = painting.getImage();

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
    int colors = prefs.getNumberOfColors();
    List<ColorModel> colorModels = new ArrayList<>(colors);

    for (int i = 0; i < colors; i++) {
      colorModels.add(new PlainColorModel(ColorUtil.getRandomColor(random)));
    }
    return colorModels;
  }

  public Preferences getPrefs() {
    return prefs;
  }


  public void setProgressListener(ProgressListener listener) {
    this.progressListener = listener;
  }

  private class ProgressCounter {
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
  }
}
