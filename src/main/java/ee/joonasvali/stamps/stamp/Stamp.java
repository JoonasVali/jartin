package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.DefaultProjectionFactory;
import ee.joonasvali.stamps.Projection;
import ee.joonasvali.stamps.ProjectionFactory;
import ee.joonasvali.stamps.properties.AppProperties;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Stamp {

  private StampGroupMetadata metadata = new StampGroupMetadata();
  private BufferedImage img = null;
  private HashMap<Color, BufferedImage> renders = new HashMap<>();
  private ProjectionFactory factory = DEFAULT_FACTORY;

  private static ProjectionFactory DEFAULT_FACTORY = new DefaultProjectionFactory();
  private static Map<String, Stamp> cache = new HashMap<>();
  private Loader loader;

  public Stamp(BufferedImage image) {
    this.img = image;
  }

  public void setMetadata(StampGroupMetadata metadata) {
    this.metadata = metadata;
  }

  public StampGroupMetadata getMetadata() {
    return metadata;
  }

  public synchronized static Stamp getInstance(File file) throws IllegalArgumentException {
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

  public Stamp(File file) throws IllegalArgumentException {
    loader = new Loader(file);
    if (!AppProperties.getInstance().isLazyLoading()) {
      loader.load();
    }
  }

  public Projection getProjection(Color color) {
    lazyLoad();
    BufferedImage image = renders.get(color);
    if (image == null) {
      image = factory.getRawProjection(img, color);
      renders.put(color, image);
    }
    return factory.getProjectionFromRaw(image);
  }

  private void lazyLoad() {
    if (AppProperties.getInstance().isLazyLoading() && img == null) {
      loader.load();
    }
  }

  public static void clearCache() {
    cache.clear();
  }

  public BufferedImage getImg() {
    lazyLoad();
    return deepCopy(img);
  }

  private static BufferedImage deepCopy(BufferedImage bi) {
    ColorModel cm = bi.getColorModel();
    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
    WritableRaster raster = bi.copyData(null);
    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
  }

  public void clearRenderCache() {
    renders.clear();
    // We clear the img to reload it when it is actually needed
    if (AppProperties.getInstance().isLazyLoading()) {
      img = null;
    }
  }

  class Loader {
    private File file;

    Loader(File file) {
      this.file = file;
    }

    private void load() throws IllegalArgumentException{
      try {
        img = ImageUtil.trim(ImageIO.read(file), Color.WHITE);
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("Fuck your permissions, I'm out.");
        System.exit(-1);
      }
    }
  }
}
