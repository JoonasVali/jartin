package ee.joonasvali.stamps;

import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.Pallette;
import ee.joonasvali.stamps.query.RandomQuery;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Joonas Vali
 */
public class Painting {
  private static RandomQuery<ColorModel> colorModelChooser = RandomQuery.create();
  private static RandomQuery<Color> colorChooser = RandomQuery.create();

  private final ArrayList<Projection> projections;
  private volatile BufferedImage canvas;
  private final int x, y;
  private final Pallette pallette;

  public Painting(int x, int y, Pallette pallette, int projections) {
    this.x = x;
    this.y = y;
    this.pallette = pallette;
    this.projections = new ArrayList<>(projections);
  }

  public synchronized void addProjection(Projection projection) {
    projections.add(projection);
  }

  private void paint() {
    canvas = new BufferedImage(this.x, this.y, BufferedImage.TYPE_INT_ARGB);
    ColorModel colorModel = pallette.getColor(colorModelChooser);
    Color backgroundColor = colorModel.getColor(colorChooser);

    for (int i = 0; i < this.x; i++) {
      for (int j = 0; j < this.y; j++) {
        canvas.setRGB(i, j, backgroundColor.getRGB());
      }
    }

    for (Projection projection : projections) {
      projection.paintTo(canvas);
    }
  }


  public BufferedImage getImage() {
    if (canvas == null) paint();
    return canvas;
  }
}
