package ee.joonasvali.stamps.query;

import java.util.List;
/**
 * @author Joonas Vali
 */
public class WColorQuery<ColorModel> extends PositionAwareQuery<ColorModel> {
  private final int width;

  public WColorQuery(int width) {
    this.width = width;
  }

  @Override
  protected ColorModel getUsingPosition(List<ColorModel> list) {
    int colors = list.size();
    int mid = colors / 2;
    if (x > (width / 2)) {
      return list.get((int)(Math.random() * mid) + mid);
    } else {
      return list.get((int)(Math.random() * mid));
    }
  }
}

