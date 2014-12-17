package ee.joonasvali.stamps.query;

import ee.joonasvali.stamps.color.ColorModel;

import java.util.List;

/**
 * @author Joonas Vali
 */
public interface BinaryColorScheme {
  public ColorModel get(List<ColorModel> list, int x, int y, double horison);
}
