package ee.joonasvali.stamps;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class Pallette {
  private List<Color> colors;

  public Pallette(List<Color> colorList) {
    this.colors = new ArrayList<>(colorList);
  }

  public Pallette(int colors) {
    this.colors = new ArrayList<>(colors);
    for (int i = 0; i < colors; i++) {
      int red = (int) (Math.random() * 256);
      int green = (int) (Math.random() * 256);
      int blue = (int) (Math.random() * 256);
      this.colors.add(new Color(red, green, blue));
    }
  }

  public Color getColor(Query<Color> q) {
    return q.get(colors);
  }
}
