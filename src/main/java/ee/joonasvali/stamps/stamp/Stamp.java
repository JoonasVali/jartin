/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.DefaultProjectionFactory;
import ee.joonasvali.stamps.Projection;
import ee.joonasvali.stamps.ProjectionFactory;
import ee.joonasvali.stamps.code.ThreadSafe;
import ee.joonasvali.stamps.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;


public class Stamp {
  public static final Logger log = LoggerFactory.getLogger(Stamp.class);
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
      log.error("IOException for file: " + file, e);
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
      log.error(e.getMessage(), e);
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
  public Projection getProjection(Color color) throws InterruptedException {
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
    } catch (ExecutionException e) {
      log.error("Something went wrong", e);
      System.exit(-1);
      return null;
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

  public Point size() {
    return new Point(img.getWidth(), img.getHeight());
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
      try (FileInputStream stream = new FileInputStream(file)) {
        img = ImageUtil.trim(ImageIO.read(stream), Color.WHITE);
      } catch (IOException e) {
        log.error("IOException for image " + file, e);
        System.exit(-1);
      } catch (Exception e) {
        log.error("Exception for image " + file, e);
      }
    }
  }
}
