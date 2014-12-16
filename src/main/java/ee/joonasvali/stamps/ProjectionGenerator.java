package ee.joonasvali.stamps;

import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.Pallette;

import java.awt.*;

/**
 * @author Joonas Vali
 */
public class ProjectionGenerator {

  public static final int OUT_OF_SIGHT_MARGIN = 50;

  StampProvider stamps;
  Pallette pallette;
  Query<Stamp> stampQuery;
  Query<ColorModel> colorQuery;
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
    Stamp stamp = stamps.getStamp(stampQuery);
    // Defined through stamps.properties, default = 1
    double rarity = stamp.getMetadata().getRarity();
    if(rarity < 1 && Math.random() > rarity && rarity > 0) {
      return generate();
    }

    int x = (int) (Math.random() * (canvasX + OUT_OF_SIGHT_MARGIN)) - OUT_OF_SIGHT_MARGIN;
    int y = (int) (Math.random() * (canvasY + OUT_OF_SIGHT_MARGIN)) - OUT_OF_SIGHT_MARGIN;
    double scale = 1 - Math.min(Math.random(), 0.7);
    double rotation = (int) (Math.random() * 360);

    ColorModel colorModel = pallette.getColor(colorQuery);
    Color color = colorModel.getColor(x, y, scale, rotation);
    int MULTIPLIER = 2;
    int i = (int) (Math.random() * 2 * MULTIPLIER - MULTIPLIER);
    if(i < 0) {
      for(; i < 0; i++)
        color = color.brighter();
    } else {
      for(; i > 0; i--)
        color = color.darker();
    }


    DefaultProjection img = (DefaultProjection) stamp.getProjection(color);
    img.setScale(scale);

    img.setRotation((int) (Math.random() * 360));
    img.setX(x);
    img.setY(y);
    return img;
  }
}
