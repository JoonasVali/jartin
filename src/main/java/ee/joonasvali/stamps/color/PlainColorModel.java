package ee.joonasvali.stamps.color;

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
  public Color getColor(int x, int y, double size, double rotation) {
    return color;
  }
}
