/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.DefaultProjectionFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * @author Joonas Vali
 */
public class RandomMergeComposerStrategy extends NumberedRandomComposerStrategy {

  public RandomMergeComposerStrategy(int count) {
    super(count);
  }

  @Override
  protected Stamp create(Stamp s1, Stamp s2) {
    BufferedImage image1 = s1.getImg();
    BufferedImage image2 = s2.getImg();

    BufferedImage image = createMaxBufferedImage(s1, s2);
    int maxDimension = Math.max(image.getWidth(), image.getHeight());
    Graphics2D iGraph = (Graphics2D) image.getGraphics();
    iGraph.setPaint (Color.WHITE);
    iGraph.fillRect (0, 0, image.getWidth(), image.getHeight());

    // Get transparent black stamp to cover the first one
    image2 = DefaultProjectionFactory.getRawProjectionImage(image2, Color.BLACK);

    draw(image1, maxDimension, iGraph);
    draw(image2, maxDimension, iGraph);

//    DEBUG
//    JFrame frame = new JFrame();
//    frame.getContentPane().add(new JLabel(new ImageIcon(image)));
//    frame.pack();
//    frame.setVisible(true);

    return new Stamp(image);
  }

  private BufferedImage transparent(BufferedImage image2) {
    return null;
  }

  private void draw(BufferedImage image1, int maxDimension, Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    AffineTransform transform = new AffineTransform();
    double scale = Math.max(Math.random(), 0.8);

    int x = (int) (maxDimension / 2 - (image1.getWidth() * scale) / 2);
    int y = (int) (maxDimension / 2 - (image1.getHeight() * scale) / 2);

    //maxDimension = (int) (maxDimension * scale);

    transform.setToTranslation(x, y);
    transform.scale(scale, scale);
    transform.rotate(Math.toRadians(Math.random() * 360), image1.getWidth() / 2d, image1.getHeight() / 2d);


    g.drawImage(image1, transform, null);
  }


}
