/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps;

import ee.joonasvali.stamps.color.PositionAwareColor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DefaultProjectionFactory implements ProjectionFactory {
  @Override
  public BufferedImage getRawProjection(BufferedImage img, Color color) {
    return getRawProjectionImage(img, color);
  }

  @Override
  public BufferedImage getRawProjection(BufferedImage img, Color color, int x, int y) {
    if(color instanceof PositionAwareColor) {
      return getRawProjectionImage(img, (PositionAwareColor)color, x, y);
    } else {
      return getRawProjection(img, color);
    }
  }

  public static BufferedImage getRawProjectionImage(BufferedImage img, Color color) {
    BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for (int i = 0; i < img.getHeight(); i++) {
      for (int j = 0; j < img.getWidth(); j++) {
        int rgb = img.getRGB(j, i);
        int red = (rgb >> 16) & 0x000000FF;
        int green = (rgb >> 8) & 0x000000FF;
        int blue = (rgb) & 0x000000FF;
        if (red == 255 && green == 255 && blue == 255) {
          // Transparent
          newImg.setRGB(j, i, 0);
        } else {
          int alpha = 255 - (red + green + blue) / 3;
          Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
          newImg.setRGB(j, i, c.getRGB());
        }
      }
    }
    return newImg;
  }

  public static BufferedImage getRawProjectionImage(BufferedImage img, PositionAwareColor color, int x, int y) {
    BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for (int i = 0; i < img.getHeight(); i++) {
      for (int j = 0; j < img.getWidth(); j++) {
        int rgb = img.getRGB(j, i);
        int red = (rgb >> 16) & 0x000000FF;
        int green = (rgb >> 8) & 0x000000FF;
        int blue = (rgb) & 0x000000FF;
        if (red == 255 && green == 255 && blue == 255) {
          // Transparent
          newImg.setRGB(j, i, 0);
        } else {
          Color awareColor = color.getColor(x + j, y + i, newImg.getWidth(), newImg.getHeight());
          int alpha = 255 - (red + green + blue) / 3;
          Color c = new Color(awareColor.getRed(), awareColor.getGreen(), awareColor.getBlue(), alpha);
          newImg.setRGB(j, i, c.getRGB());
        }
      }
    }
    return newImg;
  }

  @Override
  public Projection getProjectionFromRaw(BufferedImage image) {
    return new DefaultProjection(image);
  }
}
