/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.color;

import ee.joonasvali.stamps.query.Query;

import java.awt.*;

/**
 * @author Joonas Vali
 */
public class GradientColorModel implements PositionAwareColorModel {
  private final Color upper;
  private final Color lower;

  public GradientColorModel(Color upper, Color lower) {
    this.upper = upper;
    this.lower = lower;
  }

  @Override
  public PositionAwareColor getColor() {
    return new GradientColor(lower, upper);
  }

  public Color getUpper() {
    return upper;
  }

  public Color getLower() {
    return lower;
  }

  @Override
  public Color getColor(Query<Color> query) {
    return lower;
  }

  class GradientColor extends PositionAwareColor {
    private final int lower;
    private final int upper;

    GradientColor(Color lower, Color upper) {
      super(lower.getRGB());
      this.lower = lower.getRGB();
      this.upper = upper.getRGB();
    }

    @Override
    public Color getColor(int x, int y, int imageWidth, int imageHeight) {
      double ratio = (double)y / (double)imageHeight;
      return blend(lower, upper, ratio);

//      int anti = 100 - yPercentage;
//      yPercentage /= 100;
//      anti /= 100;
//
//
//      int lowerRed = (lower >> 16) & 0x000000FF;
//      int lowerGreen = (lower >> 8) & 0x000000FF;
//      int lowerBlue = (lower) & 0x000000FF;
//
//      int upperRed = (upper >> 16) & 0x000000FF;
//      int upperGreen = (upper >> 8) & 0x000000FF;
//      int upperBlue = (upper) & 0x000000FF;
//
//      int midRed = (lowerRed * yPercentage + upperRed * anti);
//      int midGreen = (lowerGreen * yPercentage + upperGreen * anti);
//      int midBlue = (lowerBlue * yPercentage + upperBlue * anti);
//
//      return new Color(midRed, midGreen, midBlue);
    }
  }

  private static Color blend(int i1, int i2, double ratio) {
    assert ratio <= 1;
    assert ratio >= 0;
    if ( ratio > 1d ) ratio = 1d;
    else if ( ratio < 0d ) ratio = 0d;
    double iRatio = 1.0d - ratio;


    int a1 = (i1 >> 24 & 0xff);
    int r1 = ((i1 & 0xff0000) >> 16);
    int g1 = ((i1 & 0xff00) >> 8);
    int b1 = (i1 & 0xff);

    int a2 = (i2 >> 24 & 0xff);
    int r2 = ((i2 & 0xff0000) >> 16);
    int g2 = ((i2 & 0xff00) >> 8);
    int b2 = (i2 & 0xff);

    int a = (int)((a1 * iRatio) + (a2 * ratio));
    int r = (int)((r1 * iRatio) + (r2 * ratio));
    int g = (int)((g1 * iRatio) + (g2 * ratio));
    int b = (int)((b1 * iRatio) + (b2 * ratio));

    return new Color( a << 24 | r << 16 | g << 8 | b );
  }
//
//  public static void main(String[] args) {
//    Color color = new Color(100, 100, 200);
//    Color color2 = new Color(200, 200, 100);
//    for(int i = 0; i < 100; i++) {
//      System.out.println(blend(color.getRGB(), color2.getRGB(), (double)i / 100d));
//    }
//
//  }
}

