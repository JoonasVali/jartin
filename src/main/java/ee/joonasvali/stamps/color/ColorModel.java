package ee.joonasvali.stamps.color;

import ee.joonasvali.stamps.Query;

import java.awt.*;

/**
 * @author Joonas Vali
 */
public interface ColorModel {
  public Color getColor(Query<Color> query);
}
