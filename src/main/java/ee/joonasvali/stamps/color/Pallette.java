/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.color;

import ee.joonasvali.stamps.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class Pallette {
  private final List<ColorModel> colors;

  public Pallette(List<ColorModel> colorModels) {
    this.colors = Collections.synchronizedList(new ArrayList<>(colorModels));
  }

  public ColorModel getColor(Query<ColorModel> q) {
    return q.get(colors);
  }

  public List<ColorModel> getModels() {
    synchronized (colors) {
      return new ArrayList<>(colors);
    }
  }
}
