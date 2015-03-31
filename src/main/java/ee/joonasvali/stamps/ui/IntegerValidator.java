/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.ui;

import java.util.function.Supplier;

/**
 * @author Joonas Vali
 */
public class IntegerValidator implements Validator{
  private final int minValue;
  private final int maxValue;
  private final String name;
  private final Supplier<Integer> input;

  public IntegerValidator(Supplier<Integer> getter, int minValue, int maxValue, String name) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.name = name;
    this.input = getter;
  }

  @Override
  public String validate() {
    int val;
    try {
      val = input.get();
    } catch (Exception e) {
      return name + " has an invalid value: '" + input.get() + "'";
    }
    if (minValue > val) {
      return name + " value " + val + " can't be smaller than " + minValue;
    }

    if (maxValue < val) {
      return name + " value " + val + " can't be larger than " + maxValue;
    }
    return "";
  }
}
