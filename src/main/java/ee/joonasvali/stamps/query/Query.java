/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public interface Query<T> {
  /**
   *
   * @param list the list of candidates
   * @return always return one of the elements from the list.
   * @throws RuntimeException if list is empty
   */
  public T get(List<T> list);
}
