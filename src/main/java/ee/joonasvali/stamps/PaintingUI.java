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
  public static final int STAMP_COUNT_DEMULTIPLIER = 700;
  public static final int STAMP_GROUPS_COUNT = 4;
  public static final int STAMPS_PER_GROUP = 15;

  static GroupedStamps stampPool = new GroupedStamps(new File(Main.class.getResource("/stamps").getFile()));

  public PaintingUI () {
    Stamps stamps = generateNewStamps();
    BufferedImage canvas = init(stamps);
    this.add(new JLabel(new ImageIcon(canvas)));
  }

  public void onReinit() {
    clearCaches();
    BufferedImage img = init(generateNewStamps());
    this.removeAll();
    this.add(new JLabel(new ImageIcon(img)));
  }

  private static void clearCaches() {
    Stamp.clearCache();
    stampPool.clearCaches();
  }

  private static Stamps generateNewStamps() {
    return stampPool.getStamps(STAMP_GROUPS_COUNT, STAMPS_PER_GROUP, new RandomQuery<>(), new RandomQuery<>(), false);
  }

  private static BufferedImage init(Stamps stamps) {
    int x = WIDTH;
    int y = HEIGHT;
    CompositeStamps compositeStamps = new CompositeStamps(stamps, new RandomComposerStrategy(5));

    Pallette pallette = new Pallette(NUMBER_OF_COLORS);

    ProjectionGenerator gen = new ProjectionGenerator(x, y, compositeStamps, pallette);

    Painting painting = new Painting(x, y, pallette);

    int projections = (int) (x * y / STAMP_COUNT_DEMULTIPLIER);

    System.out.println(projections);
    for (int i = 0; i < projections; i++) {
      painting.addProjection(gen.generate());
    }

    BufferedImage canvas = painting.getImage();
    return canvas;

  }
}
