package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.DefaultProjectionFactory;
import ee.joonasvali.stamps.Projection;
import ee.joonasvali.stamps.ProjectionFactory;
import ee.joonasvali.stamps.code.ThreadSafe;
import ee.joonasvali.stamps.properties.AppProperties;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;


public class Stamp {

  private volatile StampGroupMetadata metadata = new StampGroupMetadata();
  private volatile BufferedImage img = null;
  private final ConcurrentHashMap<Color, Future<BufferedImage>> renders = new ConcurrentHashMap<>();
  private final ProjectionFactory factory = DEFAULT_FACTORY;

  private static final ProjectionFactory DEFAULT_FACTORY = new DefaultProjectionFactory();
  private static final ConcurrentHashMap<String, Future<Stamp>> cache = new ConcurrentHashMap<>();
  private final Loader loader;

  public Stamp(BufferedImage image) {
    this.img = image;
    loader = null;
  }

  public void setMetadata(StampGroupMetadata metadata) {
    this.metadata = metadata;
  }

  public StampGroupMetadata getMetadata() {
    return metadata;
  }

  @ThreadSafe
  public static Stamp getInstance(File file) throws IllegalArgumentException {
    String path = null;
    try {
      path = file.getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Fuck your permissions, I'm out.");
      System.exit(-1);
    }
    Future<Stamp> stamp = cache.get(path);

    if (stamp == null) {
      Callable<Stamp> stampInitialization = () -> {
        Stamp result = new Stamp(file);
        if (!AppProperties.getInstance().isLazyLoading()) {
          result.loader.load();
        }
        return result;
      };

      FutureTask<Stamp> st = new FutureTask<>(stampInitialization);
      stamp = cache.putIfAbsent(path, st);
      if (stamp == null) {
        stamp = st;
        st.run();
      }
    }
    try {
      return stamp.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      System.exit(-1);
      // Satisfy compiler;
      return null;
    }
  }

  private Stamp(File file) throws IllegalArgumentException {
    loader = new Loader(file);
  }

  public Projection getProjection(Color color, int x, int y) {
    return factory.getProjectionFromRaw(factory.getRawProjection(img, color, x, y));
  }

  @ThreadSafe
  public Projection getProjection(Color color) {
    lazyLoad();
    Future<BufferedImage> image = renders.get(color);
    if (image == null) {
      Callable<BufferedImage> callable = () -> factory.getRawProjection(img, color);
      FutureTask<BufferedImage> ft = new FutureTask<>(callable);
      image = renders.putIfAbsent(color, ft);
      if (image == null) {
        image = ft;
        ft.run();
      }
    }

    try {
      return factory.getProjectionFromRaw(image.get());
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
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
