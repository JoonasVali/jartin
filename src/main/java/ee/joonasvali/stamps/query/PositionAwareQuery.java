/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public abstract class PositionAwareQuery<T> implements Query<T> {
  protected volatile int x;
  protected volatile int y;
  protected volatile double size;
  protected volatile double rotation;
  protected volatile boolean argumentsSet = false;

  public void provide(int x, int y, double size, double rotation) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.rotation = rotation;
    argumentsSet = true;
  }

  @Override
  public T get(List<T> list) {
    if (list.isEmpty()) throw new RuntimeException("provided list empty");
    if (!argumentsSet) throw new IllegalStateException("Must provide arguments first!");
    return getUsingPosition(list);
  }

  protected abstract T getUsingPosition(List<T> list);
}
