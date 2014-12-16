package ee.joonasvali.stamps;

import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.Pallette;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Joonas Vali
 */
public class Painting {
  private static RandomQuery<ColorModel> colorModelChooser = RandomQuery.create();
  private static RandomQuery<Color> colorChooser = RandomQuery.create();

  private ArrayList<Projection> projections = new ArrayList<>(200);
  private BufferedImage canvas;
  private int x, y;
  private Pallette pallette;

  public Painting(int x, int y, Pallette pallette) {
    this.x = x;
    this.y = y;
    this.pallette = pallette;
  }

  public void addProjection(Projection projection) {
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
