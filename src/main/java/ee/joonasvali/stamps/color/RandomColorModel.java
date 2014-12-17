package ee.joonasvali.stamps.color;


import ee.joonasvali.stamps.query.Query;

import java.awt.*;


/**
 * @author Joonas Vali
 */
public class RandomColorModel implements ColorModel {
  @Override
  public Color getColor(Query<Color> query) {
    return ColorUtil.getRandomColor();
  }
}
