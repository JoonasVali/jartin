package ee.joonasvali.stamps.query;

import ee.joonasvali.stamps.color.ColorModel;

import java.util.List;

/**
 * @author Joonas Vali
 */
public abstract class XYFormulaQuery extends PositionAwareQuery<ColorModel> {
  BinaryColorScheme scheme;

  public XYFormulaQuery(BinaryColorScheme scheme) {
    this.scheme = scheme;
  }

  @Override
  protected ColorModel getUsingPosition(List<ColorModel> list) {
    return scheme.get(list, x, y, get(x));
  }

  /**
   * Formula to calculate Y from X
   * @param x param
   * @return y param
   */
  protected abstract double get(int x);


}
