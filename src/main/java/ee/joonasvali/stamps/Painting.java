package ee.joonasvali.stamps;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Joonas Vali
 */
public class Painting {
  private ArrayList<Projection> projections = new ArrayList<Projection>(200);
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
    RandomQuery<Color> colorChooser = new RandomQuery<>();
    Color color = pallette.getColor(colorChooser);

    for (int i = 0; i < this.x; i++) {
      for (int j = 0; j < this.y; j++) {
        canvas.setRGB(i, j, color.getRGB());
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
