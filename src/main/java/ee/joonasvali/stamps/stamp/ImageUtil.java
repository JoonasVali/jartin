/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.stamp;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Joonas Vali
 */
public class ImageUtil {
  public static BufferedImage trim(BufferedImage image, Color croppedColor) throws IllegalArgumentException {
    int startX = -1, startY = -1, endX = -1, endY = -1;
    int color = croppedColor.getRGB();
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        if(image.getRGB(x, y) != color) {
          //
          if(startX == -1 || startX > x) {
            startX = x;
          }
          if(startY == -1 || startY > y) {
            startY = y;
          }

          endX = Math.max(x, endX);
          endY = Math.max(y, endY);
        }
      }
    }

    if(endX <= startX || endY <= startY) {
      throw new IllegalArgumentException("Image is empty when trimmed");
    }
    BufferedImage img = image.getSubimage(startX, startY, endX - startX, endY - startY); //fill in the corners of the desired crop location here
    BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g = copyOfImage.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.drawImage(img, 0, 0, null);
    return copyOfImage;
  }
}
