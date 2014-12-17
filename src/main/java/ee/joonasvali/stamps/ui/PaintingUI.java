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
import ee.joonasvali.stamps.color.RandomColorModel;
import ee.joonasvali.stamps.properties.AppProperties;
import ee.joonasvali.stamps.query.Query;
import ee.joonasvali.stamps.query.RandomQuery;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Joonas Vali
 */
public class PaintingUI extends JPanel {

  public static final double CHANCE_OF_RANDOM_COLOR_MODEL = 0.1;
  private boolean retainColors = false;
  private boolean retainStamps = false;
  private Preferences prefs = new Preferences();


  private Pallette pallette;
  private Stamps stamps;

  private BufferedImage lastImage;

  static GroupedStamps stampPool = new GroupedStamps(AppProperties.getInstance().getStampsDir());

  public PaintingUI() {
    init();
    this.add(new JLabel(new ImageIcon(lastImage)));
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

  public void onReinit() {
    clearCaches();
    init();
    this.removeAll();
    this.add(new JLabel(new ImageIcon(lastImage)));
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

  private void init() {
    int x = prefs.getWidth();
    int y = prefs.getHeight();

    stamps = createStamps();
    CompositeStamps compositeStamps = new CompositeStamps(stamps, new RandomComposerStrategy(5));

    if (pallette == null || !retainColors) {
      pallette = new Pallette(generateColorModels(new Random()));
    }

    ProjectionGenerator gen = new ProjectionGenerator(x, y, compositeStamps, pallette);

    Painting painting = new Painting(x, y, pallette);

    int projections = (x * y / prefs.getStampCountDemultiplier());

    Query<Stamp> stampQuery = RandomQuery.create();
    Query<ColorModel> colorModelQuery = RandomQuery.create();
    Query<Color> colorQuery = RandomQuery.create();


    for (int i = 0; i < projections; i++) {
      painting.addProjection(gen.generate(stampQuery, colorModelQuery, colorQuery));
    }

    lastImage = painting.getImage();
  }

  private List<ColorModel> generateColorModels(Random random) {
    int colors = prefs.getNumberOfColors();
    List<ColorModel> colorModels = new ArrayList<>(colors);

    for (int i = 0; i < colors; i++) {
      if (Math.random() < CHANCE_OF_RANDOM_COLOR_MODEL) {
        colorModels.add(new RandomColorModel());
      } else {
        colorModels.add(new PlainColorModel(ColorUtil.getRandomColor(random)));
      }
    }
    return colorModels;
  }

  public Preferences getPrefs() {
    return prefs;
  }
}
