package ee.joonasvali.stamps;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface ProjectionFactory {
  public BufferedImage getRawProjection(BufferedImage image, Color color);
  public Projection getProjectionFromRaw(BufferedImage image);
}
