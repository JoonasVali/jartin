/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface ProjectionFactory {
  public BufferedImage getRawProjection(BufferedImage image, Color color, int x, int y);
  public BufferedImage getRawProjection(BufferedImage image, Color color);
  public Projection getProjectionFromRaw(BufferedImage image);
}
