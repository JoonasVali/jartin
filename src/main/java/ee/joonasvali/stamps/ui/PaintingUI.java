package ee.joonasvali.stamps.ui;

import ee.joonasvali.stamps.AppProperties;
import ee.joonasvali.stamps.CompositeStamps;
import ee.joonasvali.stamps.GroupedStamps;
import ee.joonasvali.stamps.Painting;
import ee.joonasvali.stamps.Pallette;
import ee.joonasvali.stamps.ProjectionGenerator;
import ee.joonasvali.stamps.RandomComposerStrategy;
import ee.joonasvali.stamps.RandomQuery;
import ee.joonasvali.stamps.Stamp;
import ee.joonasvali.stamps.Stamps;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * @author Joonas Vali
 */
public class PaintingUI extends JPanel {

  private boolean retainColors = false;
  private boolean retainStamps = false;
  private Preferences prefs = new Preferences();


  private Pallette pallette;
  private Stamps stamps;

  private BufferedImage lastImage;

  static GroupedStamps stampPool = new GroupedStamps(AppProperties.getInstance().getStampsDir());

  public PaintingUI () {
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
      return stampPool.getStamps(prefs.getStampGroupsCount(), prefs.getStampsPerGroup(), new RandomQuery<>(), new RandomQuery<>(), false);
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
      pallette = new Pallette(prefs.getNumberOfColors());
    }

    ProjectionGenerator gen = new ProjectionGenerator(x, y, compositeStamps, pallette);

    Painting painting = new Painting(x, y, pallette);

    int projections = (x * y / prefs.getStampCountDemultiplier());

    for (int i = 0; i < projections; i++) {
      painting.addProjection(gen.generate());
    }

    lastImage = painting.getImage();
  }

  public Preferences getPrefs() {
    return prefs;
  }
}
