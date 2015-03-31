/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.color;

import ee.joonasvali.stamps.query.Query;

import java.awt.*;

/**
 * @author Joonas Vali
 */
public class PlainColorModel implements ColorModel {
  private final Color color;

  public PlainColorModel(Color color) {
    this.color = color;
  }

  @Override
  public Color getColor(Query<Color> query) {
    return color;
  }
}
