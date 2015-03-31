/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Joonas Vali
 */
public enum BinaryValue {
  ZERO(false, 0), ONE(true, 1);

  private static List<BinaryValue> values;
  static {
    values = new ArrayList<>();
    values.add(ZERO);
    values.add(ONE);
    values = Collections.unmodifiableList(values);
  }

  public static BinaryValue get(Query<BinaryValue> query) {
    return query.get(values);
  }

  private int intValue;
  private boolean booleanValue;

  private BinaryValue(boolean booleanValue, int intValue) {
    this.intValue = intValue;
    this.booleanValue = booleanValue;
  }

  public int getIntValue() {
    return intValue;
  }

  public boolean getBooleanValue() {
    return booleanValue;
  }

  public static List<BinaryValue> getValues() {
    return values;
  }
}
