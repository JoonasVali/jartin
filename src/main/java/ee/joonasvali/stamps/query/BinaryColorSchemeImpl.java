package ee.joonasvali.stamps.query;

import ee.joonasvali.stamps.color.ColorModel;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class BinaryColorSchemeImpl implements BinaryColorScheme {
  @Override
  public ColorModel get(List<ColorModel> list, int x, int y, double horison) {
    boolean over = y > horison;
    if (over) {

    } else {

    }
    return null;
  }
}
