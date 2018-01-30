/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class BinaryQuery<T> {
  private final double division;
  /**
   * @param division 0 < x < 1
   */
  public BinaryQuery(double division) {
    if(division <= 0 || division >= 1) {
      throw new IllegalArgumentException("Division can't be " + division + " must be between 0 and 1");
    }
    this.division = division;
  }

  public T get(List<T> list, BinaryValue binaryValue, Query<T> query) {
    int posSize = (int) (list.size() * division);
    int size = 0;
    int startPos = 0;
    if (BinaryValue.ZERO.equals(binaryValue)) {
      size = posSize;
      startPos = 0;
      size = Math.max(size, 1);
    } else {
      posSize = Math.max(posSize, 1);
      size = list.size() - posSize;
      startPos = posSize;
    }
    return query.get(list.subList( startPos, startPos + size));
  }
}
