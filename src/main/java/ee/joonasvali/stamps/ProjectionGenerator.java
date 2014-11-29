package ee.joonasvali.stamps;

import java.awt.*;

/**
 * @author Joonas Vali
 */
public class ProjectionGenerator {

  public static final int OUT_OF_SIGHT_MARGIN = 50;

  StampProvider stamps;
  Pallette pallette;
  Query<Stamp> stampQuery;
  Query<Color> colorQuery;
  int canvasX;
  int canvasY;

  public ProjectionGenerator(int canvasX, int canvasY, StampProvider stamps, Pallette pallette) {
    this.stamps = stamps;
    this.pallette = pallette;
    this.canvasX = canvasX;
    this.canvasY = canvasY;
    this.stampQuery = new RandomQuery<>();
    this.colorQuery = new RandomQuery<>();
  }

  public Projection generate() {
    int x = (int) (Math.random() * (canvasX + OUT_OF_SIGHT_MARGIN)) - OUT_OF_SIGHT_MARGIN;
    int y = (int) (Math.random() * (canvasY + OUT_OF_SIGHT_MARGIN)) - OUT_OF_SIGHT_MARGIN;

    Color color = pallette.getColor(colorQuery);
    int i = (int) (Math.random() * 4 - 2);
    if(i < 0) {
      for(; i < 0; i++)
        color = color.brighter();
    } else {
      for(; i > 0; i--)
        color = color.darker();
    }

    DefaultProjection img = (DefaultProjection) stamps.getStamp(stampQuery).getProjection(color);
    img.setScale(1 - Math.min(Math.random(), 0.7));

    img.setRotation((int) (Math.random() * 360));
    img.setX(x);
    img.setY(y);
    return img;
  }
}
