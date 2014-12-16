package ee.joonasvali.stamps.color;

import java.awt.*;

/**
 * @author Joonas Vali
 */
public interface ColorModel {
  public Color getColor(int x, int y, double size, double rotation);
}
