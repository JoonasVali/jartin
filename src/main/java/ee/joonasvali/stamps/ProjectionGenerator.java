package ee.joonasvali.stamps;

import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.PositionAwareColorModel;
import ee.joonasvali.stamps.color.Pallette;
import ee.joonasvali.stamps.query.PositionAwareQuery;
import ee.joonasvali.stamps.query.Query;
import ee.joonasvali.stamps.stamp.Stamp;
import ee.joonasvali.stamps.stamp.StampProvider;

import java.awt.*;

/**
 * @author Joonas Vali
 */
public class ProjectionGenerator {

  public static final int OUT_OF_SIGHT_MARGIN = 150;
  public static final double SCALE_MIN_VALUE = 0.3;

  private final StampProvider stamps;
  private final Pallette pallette;
  private final int canvasX;
  private final int canvasY;

  public ProjectionGenerator(int canvasX, int canvasY, StampProvider stamps, Pallette pallette) {
    this.stamps = stamps;
    this.pallette = pallette;
    this.canvasX = canvasX;
    this.canvasY = canvasY;
  }

  public Projection generate(Query<Stamp> stampQuery, Query<ColorModel> colorModelQuery, Query<Color> colorQuery) {
    // This is the actual place where calculation of every projection scale, rotation and position takes place
    int x = (int) (Math.random() * (canvasX + OUT_OF_SIGHT_MARGIN)) - OUT_OF_SIGHT_MARGIN;
    int y = (int) (Math.random() * (canvasY + OUT_OF_SIGHT_MARGIN)) - OUT_OF_SIGHT_MARGIN;
    double scale = Math.max(Math.random(), SCALE_MIN_VALUE);
    double rotation = (int) (Math.random() * 360);

    // We provide the calculated positions for each query, so it will be position aware and can make decision based on it.
    providePositions(stampQuery, x, y, scale, rotation);
    providePositions(colorModelQuery, x, y, scale, rotation);
    providePositions(colorQuery, x, y, scale, rotation);

    Stamp stamp = stamps.getStamp(stampQuery);
    // Defined through stamps.properties, default = 1
    double rarity = stamp.getMetadata().getRarity();
    if(rarity < 1 && Math.random() > rarity && rarity > 0) {
      return generate(stampQuery, colorModelQuery, colorQuery);
    }

    DefaultProjection projection;
    ColorModel colorModel = pallette.getColor(colorModelQuery);
    if (colorModel instanceof PositionAwareColorModel) {
      PositionAwareColorModel mColor = (PositionAwareColorModel)colorModel;
      projection = (DefaultProjection) stamp.getProjection(mColor.getColor(), x, y);
      projection.setScale(scale);
      projection.setRotation((int) rotation);
      projection.setX(x);
      projection.setY(y);
    } else {
      Color color = colorModel.getColor(colorQuery);
      int MULTIPLIER = 2;
      int i = (int) (Math.random() * 2 * MULTIPLIER - MULTIPLIER);
      if(i < 0) {
        for(; i < 0; i++)
          color = color.brighter();
      } else {
        for(; i > 0; i--)
          color = color.darker();
      }


      projection = (DefaultProjection) stamp.getProjection(color);
      projection.setScale(scale);

      projection.setRotation((int) rotation);
      projection.setX(x);
      projection.setY(y);
    }

    return projection;
  }

  private void providePositions(Query<?> query, int x, int y, double scale, double rotation) {
    if(query instanceof PositionAwareQuery) {
      ((PositionAwareQuery) query).provide(x, y, scale, rotation);
    }
  }
}
