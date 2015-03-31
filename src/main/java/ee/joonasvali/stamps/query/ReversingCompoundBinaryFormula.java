/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.query;

import java.util.Arrays;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class ReversingCompoundBinaryFormula implements BinaryFormula {

  private final List<BinaryFormula> formulas;

  public ReversingCompoundBinaryFormula(BinaryFormula... formulas) {
    this.formulas = Arrays.asList(formulas);
  }

  @Override
  public BinaryValue get(int x, int y) {
    boolean flip = false;
    for(BinaryFormula f : formulas) {
      if (f.get(x, y).getBooleanValue()) {
        flip = !flip;
      }
    }
    return flip ? BinaryValue.ONE : BinaryValue.ZERO;
  }
}
