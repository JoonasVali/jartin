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

/**
 * @author Joonas Vali
 */
public class Painting {
  private static final Logger log = LoggerFactory.getLogger(Painting.class);
  private static final RandomQuery<Color> colorChooser = RandomQuery.create();

  private final ProjectionRenderer renderer;
  private final BufferedImage canvas;
  private final int width, height;
  private final ColorModel backgroundColorModel;

  public Painting(int width, int height, ColorModel backgroundColorModel, ProjectionRenderer renderer) {
    this.width = width;
    this.height = height;
    this.renderer = renderer;
    this.backgroundColorModel = backgroundColorModel;
    canvas = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
  }

  public BufferedImage paint(ProgressCounter counter) throws InterruptedException {
    paintBackground();
    while (renderer.hasNext()) {
      Projection projection = renderer.next();
      if (Thread.currentThread().isInterrupted()) {
        counter.clear();
        throw new InterruptedException();
      }
      projection.paintTo(canvas);
      counter.increase();
    }
    counter.clear();
    return canvas;
  }

  private void paintBackground() {
    ColorModel colorModel = backgroundColorModel;

    if (colorModel instanceof PositionAwareColorModel) {
      PositionAwareColorModel bgModel = (PositionAwareColorModel) colorModel;
      PositionAwareColor bgColor = bgModel.getColor();

      for (int i = 0; i < this.width; i++) {
        for (int j = 0; j < this.height; j++) {
          canvas.setRGB(i, j, bgColor.getColor(i, j, width, height).getRGB());
        }
      }
    } else {
      Graphics2D g = canvas.createGraphics();
      Color color = colorModel.getColor(colorChooser);
      g.setColor(color);
      g.fill(new Rectangle(0, 0, width, height));
    }
  }
}
