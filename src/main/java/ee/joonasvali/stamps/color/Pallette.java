package ee.joonasvali.stamps.color;

import ee.joonasvali.stamps.Query;

import java.awt.*;
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

  //TODO such random generator shouldn't be here
  public Pallette(int colors) {
    this.colors = new ArrayList<>(colors);
    for (int i = 0; i < colors; i++) {
      int red = (int) (Math.random() * 256);
      int green = (int) (Math.random() * 256);
      int blue = (int) (Math.random() * 256);
      this.colors.add(new PlainColorModel(new Color(red, green, blue)));
    }
  }

  public ColorModel getColor(Query<ColorModel> q) {
    return q.get(colors);
  }
}
