/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.painting;

import ee.joonasvali.stamps.Projection;
import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.PositionAwareColor;
import ee.joonasvali.stamps.color.PositionAwareColorModel;
import ee.joonasvali.stamps.query.RandomQuery;
import ee.joonasvali.stamps.ui.ProgressCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Joonas Vali
 */
public class Painting {
  public static final Logger log = LoggerFactory.getLogger(Painting.class);
  private static final Projection POISON_PILL = canvas -> { /* Nothing to do */ };
  private static RandomQuery<Color> colorChooser = RandomQuery.create();

  private final ArrayBlockingQueue<Projection> projections;
  private volatile BufferedImage canvas;
  private final int width, height;
  private volatile boolean isPainting;
  private final SynchronousQueue<BufferedImage> canvasSync;
  private final ColorModel backgroundColorModel;
  private volatile ProgressCounter counter;

  public Runnable startPainting(ProgressCounter counter) {
    this.counter = counter;
    if (isPainting) {
      log.error("Illegal state in Painting, can't call startPainting");
      System.exit(-1);
    }
    isPainting = true;
    return getAction();
  }

  public void stopPainting() throws InterruptedException {
    if (!isPainting) {
      throw new InterruptedException("Painting already stopped");
    }
    isPainting = false;

    try {
      projections.put(POISON_PILL);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      System.exit(-1);
    }
  }

  public Painting(int width, int height, ColorModel backgroundColorModel, int projections) {
    this.width = width;
    this.height = height;
    this.projections = new ArrayBlockingQueue<>(projections + 1);
    this.canvasSync = new SynchronousQueue<>();
    this.backgroundColorModel = backgroundColorModel;
  }


  public void addProjection(Projection projection) {
    try {
      projections.put(projection);
    } catch (InterruptedException e) {
      return;
    }
  }

  private void paintBackground() {
    canvas = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
    ColorModel colorModel = backgroundColorModel;

    if (colorModel instanceof PositionAwareColorModel) {
      PositionAwareColorModel bgModel = (PositionAwareColorModel) colorModel;
      PositionAwareColor bgColor = bgModel.getColor();

      for (int i = 0; i < this.width; i++) {
        for (int j = 0; j < this.height; j++) {
          canvas.setRGB(i, j, bgColor.getColor(i, j).getRGB());
        }
      }
    } else {
      Graphics2D g = canvas.createGraphics();
      Color color = colorModel.getColor(colorChooser);
      g.setColor(color);
      g.fill(new Rectangle(0, 0, width, height));
    }
  }

  public void cancel() {
    projections.clear();
    try {
      stopPainting();
    } catch (InterruptedException e) {
      log.error("This shouldn't happen", e);
      System.exit(-1);
    }
  }

  public BufferedImage getImage() throws InterruptedException {
    return canvasSync.take();
  }

  public Runnable getAction() {
    return () -> {
      paintBackground();
      while (isPainting || projections.size() > 0) {
        try {
          Projection projection = projections.take();
          if (projection == POISON_PILL) break;
          projection.paintTo(canvas);
          counter.increase();
        } catch (InterruptedException e) {
          counter.clear();
          return;
        }
      }

      counter.clear();
      try {
        canvasSync.put(canvas);
      } catch (InterruptedException ignore) {
        return;
      }
    };
  }
}
