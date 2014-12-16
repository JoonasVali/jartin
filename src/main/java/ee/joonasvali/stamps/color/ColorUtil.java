package ee.joonasvali.stamps.color;

import java.awt.*;
import java.util.Random;

/**
 * @author Joonas Vali
 */
public class ColorUtil {
  private static Random DEFAULT_RANDOM = new Random();

  public static Color getRandomColor() {
    return getRandomColor(DEFAULT_RANDOM);
  }

  public static Color getRandomColor(Random random) {
    int red = (int) (random.nextDouble() * 256);
    int green = (int) (random.nextDouble() * 256);
    int blue = (int) (random.nextDouble() * 256);
    return new Color(red, green, blue);
  }
}
