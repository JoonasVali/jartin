/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.ui;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joonas Vali
 */
public class ProgressCounter {
  private AtomicInteger count = new AtomicInteger();
  private ProgressListener listener;
  private int totalProjections;

  public ProgressCounter(ProgressListener listener, int totalProjections) {
    this.listener = listener;
    this.totalProjections = totalProjections;
  }

  public void increase() {
    int val = (int)((double)count.incrementAndGet() / (double)totalProjections * 100);
    listener.setValue(val);
  }

  public void clear() {
    count.set(0);
    listener.setValue(0);
  }

  public void setValue(String message) {
    listener.setValue(message);
  }
}
