/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * @author Joonas Vali
 */
public class Util {
  public static final Logger log = LoggerFactory.getLogger(Util.class);
  public static void assertEDT() {
    if (!SwingUtilities.isEventDispatchThread()) {
      log.error("Expected EDT", new RuntimeException("Wrong thread, expected EDT"));
      System.exit(-1);
    }
  }

  public static String getUserDir() {
    return System.getProperty("user.dir");
  }
}
