/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.color;

import java.awt.*;
import java.util.Random;

/**
 * @author Joonas Vali
 */
public class ColorUtil {
  private static final Random DEFAULT_RANDOM = new Random();

  public static Color getRandomColor() {
    return getRandomColor(DEFAULT_RANDOM);
  }

  public static Color getRandomColor(Random random) {
    int red = (int) (random.nextDouble() * 256);
    int green = (int) (random.nextDouble() * 256);
    int blue = (int) (random.nextDouble() * 256);
    return new Color(red, green, blue);
  }
}
