/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class XYFormulaQuery<C> extends PositionAwareQuery<C> implements Query<C> {
  private final Query<C> query;
  private final BinaryQuery<C> binaryQuery;
  private final BinaryFormula formula;

  public XYFormulaQuery(Query<C> query, BinaryQuery<C> binaryQuery, BinaryFormula formula) {
    this.query = query;
    this.binaryQuery = binaryQuery;
    this.formula = formula;
  }

  @Override
  protected C getUsingPosition(List<C> list) {
    return binaryQuery.get(list, formula.get(x, y), query);
  }

  public BinaryFormula getFormula() {
    return formula;
  }
}
