/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.color;

import java.awt.*;
import java.awt.color.ColorSpace;

/**
 * @author Joonas Vali
 */
public abstract class PositionAwareColor extends Color {
  public PositionAwareColor(int r, int g, int b) {
    super(r, g, b);
  }

  protected PositionAwareColor(int r, int g, int b, int a) {
    super(r, g, b, a);
  }

  protected PositionAwareColor(int rgb) {
    super(rgb);
  }

  protected PositionAwareColor(int rgba, boolean hasalpha) {
    super(rgba, hasalpha);
  }

  protected PositionAwareColor(float r, float g, float b) {
    super(r, g, b);
  }

  protected PositionAwareColor(float r, float g, float b, float a) {
    super(r, g, b, a);
  }

  protected PositionAwareColor(ColorSpace cspace, float[] components, float alpha) {
    super(cspace, components, alpha);
  }

  public abstract Color getColor(int x, int y, int imageWidth, int imageHeight);

}
