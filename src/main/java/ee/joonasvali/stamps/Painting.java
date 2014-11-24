package ee.joonasvali.stamps;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Joonas Vali
 */
public class Painting {
  private ArrayList<Projection> projections = new ArrayList<Projection>(200);
  private BufferedImage canvas;
  private int x, y;

  public Painting(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void addProjection(Projection projection) {
    projections.add(projection);
  }

  private void paint() {
    canvas = new BufferedImage(this.x, this.y, BufferedImage.TYPE_INT_ARGB);

    for (Projection projection : projections) {
      projection.paintTo(canvas);
    }
  }


  public BufferedImage getImage() {
    if(canvas == null) paint();
    return canvas;
  }
}
