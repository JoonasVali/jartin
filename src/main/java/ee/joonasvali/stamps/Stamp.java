package ee.joonasvali.stamps;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Stamp {

  private BufferedImage img = null;
  private HashMap<Color, BufferedImage> renders = new HashMap<>();
  private ProjectionFactory factory = DEFAULT_FACTORY;

  private static ProjectionFactory DEFAULT_FACTORY = new DefaultProjectionFactory();
  private static Map<String, Stamp> cache = new HashMap<>();

  public synchronized static Stamp getInstance(File file) {
    String path = null;
    try {
      path = file.getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Fuck your permissions, I'm out.");
      System.exit(-1);
    }
    Stamp stamp = cache.get(path);

    if (stamp == null) {
      stamp = new Stamp(file);
      cache.put(path, stamp);
    }
    return stamp;
  }

  public Stamp(File file) {
    try {
      img = ImageIO.read(file);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Fuck your permissions, I'm out.");
      System.exit(-1);
    }
  }

  public Projection getProjection(Color color) {
    BufferedImage image = renders.get(color);
    if (image == null) {
      image = factory.getRawProjection(img, color);
      renders.put(color, image);
    }
    return factory.getProjectionFromRaw(image);
  }
}
