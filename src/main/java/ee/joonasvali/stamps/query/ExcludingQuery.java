/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Joonas Vali
 */
public class ExcludingQuery<T> implements Query<T> {
  private Set<T> exclude;
  private Query<T> query;

  public ExcludingQuery(Query<T> stampQuery, Collection<T> excluded) {
    this.exclude = new HashSet<>(excluded);
    this.query = stampQuery;
  }

  /**
   *
   * @param list list of candidates
   * @return one of the elements that is not excluded. In case all of the elements in provided
   * list are excluded, we return one of the excluded elements as a fallback strategy.
   */
  @Override
  public T get(List<T> list) {
    if (list.isEmpty()) throw new RuntimeException("provided list empty");
    List<T> candidates = new ArrayList<>(list.size());
    for (T ob : list) {
      if (!exclude.contains(ob)) {
        candidates.add(ob);
      }
    }
    if(candidates.size() > 0) {
      return query.get(candidates);
    } else {
      // fall back
      return query.get(list);
    }
  }

  /**
   *
   * @param ob excludes the added object
   * @return true if object was not yet excluded
   */
  public boolean addExclusion(T ob) {
    return exclude.add(ob);
  }
}
