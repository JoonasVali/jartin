package ee.joonasvali.stamps.color;

import ee.joonasvali.stamps.Query;

import java.awt.*;

/**
 * @author Joonas Vali
 */
public class PlainColorModel implements ColorModel {
  private Color color;

  public PlainColorModel(Color color) {
    this.color = color;
  }

  @Override
  public Color getColor(Query<Color> query) {
    return color;
  }
}
