package ee.joonasvali.stamps;

import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.Pallette;
import ee.joonasvali.stamps.color.PositionAwareColorModel;
import ee.joonasvali.stamps.query.DynamicExcludingQuery;
import ee.joonasvali.stamps.query.PositionAwareQuery;
import ee.joonasvali.stamps.query.Query;
import ee.joonasvali.stamps.stamp.Stamp;
import ee.joonasvali.stamps.stamp.StampProvider;

import java.awt.*;
import java.util.Random;

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
  private final Random random;

  public ProjectionGenerator(int canvasX, int canvasY, StampProvider stamps, Pallette pallette, Random random) {
    this.random = random;
    this.stamps = stamps;
    this.pallette = pallette;
    this.canvasX = canvasX;
    this.canvasY = canvasY;
  }

  public Projection generate(Query<Stamp> stampQuery, Query<ColorModel> colorModelQuery, Query<Color> colorQuery) throws InterruptedException {
    // This is the actual place where calculation of every projection scale, rotation and position takes place
    int x = (int) (random.nextDouble() * (canvasX + OUT_OF_SIGHT_MARGIN)) - OUT_OF_SIGHT_MARGIN;
    int y = (int) (random.nextDouble() * (canvasY + OUT_OF_SIGHT_MARGIN)) - OUT_OF_SIGHT_MARGIN;
    double scale = Math.max(random.nextDouble(), SCALE_MIN_VALUE);
    double rotation = (int) (random.nextDouble() * 360);

    // We provide the calculated positions for each query, so it will be position aware and can make decision based on it.
    providePositions(stampQuery, x, y, scale, rotation);
    providePositions(colorModelQuery, x, y, scale, rotation);
    providePositions(colorQuery, x, y, scale, rotation);

    // DynamicExcludingQuery helps us to get some other stamp if the "rarity check" fails
    Stamp stamp = stamps.getStamp(new DynamicExcludingQuery<>(stampQuery, ob -> {
      // Defined through stamps.properties, default = 1
      double rarity = ob.getMetadata().getRarity();
      return !(rarity < 1 && random.nextDouble() > rarity && rarity > 0);
    }));


    DefaultProjection projection;
    ColorModel colorModel = pallette.getColor(colorModelQuery);
    // TODO these decisions probably could be abstracted?
    if (colorModel instanceof PositionAwareColorModel) {
      PositionAwareColorModel mColor = (PositionAwareColorModel) colorModel;
      projection = (DefaultProjection) stamp.getProjection(mColor.getColor(), x, y);
      projection.setScale(scale);
      projection.setRotation((int) rotation);
      projection.setX(x);
      projection.setY(y);
    } else {
      Color color = colorModel.getColor(colorQuery);
      int MULTIPLIER = 2;
      int i = (int) (Math.random() * 2 * MULTIPLIER - MULTIPLIER);
      if (i < 0) {
        for (; i < 0; i++)
          color = color.brighter();
      } else {
        for (; i > 0; i--)
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
    if (query instanceof PositionAwareQuery) {
      ((PositionAwareQuery) query).provide(x, y, scale, rotation);
    }
  }
}
