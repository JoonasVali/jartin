package ee.joonasvali.stamps;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author Joonas Vali
 */
public class PaintingUI extends JPanel {
  public static final int WIDTH = 1800;
  public static final int HEIGHT = 1000;
  public static final int NUMBER_OF_COLORS = 2;
  public static final int STAMP_COUNT_DEMULTIPLIER = 1700;
  public static final int STAMP_GROUPS_COUNT = 4;
  public static final int STAMPS_PER_GROUP = 15;

  public boolean retainColors = false;
  public boolean retainStamps = false;

  private Pallette pallette;
  private Stamps stamps;

  private BufferedImage lastImage;

  static GroupedStamps stampPool = new GroupedStamps(new File(Main.class.getResource("/stamps").getFile()));

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
      return stampPool.getStamps(STAMP_GROUPS_COUNT, STAMPS_PER_GROUP, new RandomQuery<>(), new RandomQuery<>(), false);
    } else {
      return stamps;
    }
  }

  private void init() {
    int x = WIDTH;
    int y = HEIGHT;

    stamps = createStamps();
    CompositeStamps compositeStamps = new CompositeStamps(stamps, new RandomComposerStrategy(5));

    if (pallette == null || !retainColors) {
      pallette = new Pallette(NUMBER_OF_COLORS);
    }

    ProjectionGenerator gen = new ProjectionGenerator(x, y, compositeStamps, pallette);

    Painting painting = new Painting(x, y, pallette);

    int projections = (int) (x * y / STAMP_COUNT_DEMULTIPLIER);

    System.out.println(projections);
    for (int i = 0; i < projections; i++) {
      painting.addProjection(gen.generate());
    }

    lastImage = painting.getImage();
  }
}
