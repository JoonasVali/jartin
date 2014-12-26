package ee.joonasvali.stamps.color;

import ee.joonasvali.stamps.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class Pallette {
  private List<ColorModel> colors;

  public Pallette(List<ColorModel> colorModels) {
    this.colors = new ArrayList<>(colorModels);
  }

  public ColorModel getColor(Query<ColorModel> q) {
    return q.get(colors);
  }
}
