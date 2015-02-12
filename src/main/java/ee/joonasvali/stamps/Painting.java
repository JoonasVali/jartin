package ee.joonasvali.stamps;

import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.Pallette;
import ee.joonasvali.stamps.color.PositionAwareColor;
import ee.joonasvali.stamps.color.PositionAwareColorModel;
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
  private final int width, height;
  private final Pallette pallette;

  public Painting(int width, int height, Pallette pallette, int projections) {
    this.width = width;
    this.height = height;
    this.pallette = pallette;
    this.projections = new ArrayList<>(projections);
  }

  public synchronized void addProjection(Projection projection) {
    projections.add(projection);
  }

  private void paint() {
    canvas = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
    ColorModel colorModel = pallette.getColor(colorModelChooser);
    if(colorModel instanceof PositionAwareColorModel) {
      PositionAwareColorModel bgModel = (PositionAwareColorModel)colorModel;
      PositionAwareColor bgColor = bgModel.getColor();

      for (int i = 0; i < this.width; i++) {
        for (int j = 0; j < this.height; j++) {
          canvas.setRGB(i, j, bgColor.getColor(i, j).getRGB());
        }
      }
    } else {
      Color backgroundColor = colorModel.getColor(colorChooser);

      for (int i = 0; i < this.width; i++) {
        for (int j = 0; j < this.height; j++) {
          canvas.setRGB(i, j, backgroundColor.getRGB());
        }
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
