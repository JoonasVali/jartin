/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.ui;

import ee.joonasvali.stamps.code.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Joonas Vali
 *
 * // TODO, lot of duplicate code here that increases with every new setting... OOP needed
 */
public class Preferences {
  public static final Logger log = LoggerFactory.getLogger(Preferences.class);
  private static final String PREF_FILE_NAME = "pref.properties";
  private static final String ID_NUMBER_OF_COLORS = "stamp.colorbase.count";
  private static final String ID_STAMP_COUNT_MULTIPLIER = "stamp.count.multiplier";
  private static final String ID_STAMP_GROUPS_COUNT = "stamp.groups.count";
  private static final String ID_STAMPS_PER_GROUP = "stamp.groups.stamps_per_group";

  private static final int WIDTH = getMonitorWidth();
  private static final int HEIGHT = getMonitorHeight();
  private static final int NUMBER_OF_COLORS = 2;
  private static final double STAMP_COUNT_MULTIPLIER = 0.45d;
  private static final int STAMP_GROUPS_COUNT = 6;
  private static final int STAMPS_PER_GROUP = 20;
  private static final boolean SPINE_MODE = false;

  private volatile int width = WIDTH;
  private volatile int height = HEIGHT;
  private volatile int numberOfColors = NUMBER_OF_COLORS;
  private volatile double stampCountMultiplier = STAMP_COUNT_MULTIPLIER;
  private volatile int stampGroupsCount = STAMP_GROUPS_COUNT;
  private volatile int stampsPerGroup = STAMPS_PER_GROUP;
  private volatile boolean spineMode = SPINE_MODE;


  public Preferences () {
    File prefs = new File(Util.getUserDir(), PREF_FILE_NAME);
    if (!prefs.exists()) {
      log.error("Preferences file " + PREF_FILE_NAME + " not found.");
    } else {
      loadPreferences(prefs);
    }
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getNumberOfColors() {
    return numberOfColors;
  }

  public void setNumberOfColors(int numberOfColors) {
    this.numberOfColors = numberOfColors;
  }

  public double getStampCountMultiplier() {
    return stampCountMultiplier;
  }

  public void setStampCountMultiplier(double stampCountMultiplier) {
    this.stampCountMultiplier = stampCountMultiplier;
  }

  public int getStampGroupsCount() {
    return stampGroupsCount;
  }

  public void setStampGroupsCount(int stampGroupsCount) {
    this.stampGroupsCount = stampGroupsCount;
  }

  public int getStampsPerGroup() {
    return stampsPerGroup;
  }

  public void setStampsPerGroup(int stampsPerGroup) {
    this.stampsPerGroup = stampsPerGroup;
  }

  public boolean isSpineMode() {
    return spineMode;
  }

  public void setSpineMode(boolean spineMode) {
    this.spineMode = spineMode;
  }


  private static int getMonitorWidth() {
    int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    log.info("Resolution width detected: " + width + " px");
    return Math.max(width, 200);
  }

  private static int getMonitorHeight() {
    int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    log.info("Resolution height detected: " + height + " px");
    return Math.max(height, 200);
  }

  public void loadPreferences(File file) {
    Properties properties = new Properties();
    try (InputStream in = new FileInputStream(file)) {
      properties.load(in);
    } catch (IOException e) {
      log.error("Loading preferences file failed", e);
      return;
    }

    try {
      setNumberOfColors(Integer.parseInt(properties.getProperty(ID_NUMBER_OF_COLORS, Integer.toString(NUMBER_OF_COLORS))));
      setStampCountMultiplier(Integer.parseInt(properties.getProperty(ID_STAMP_COUNT_MULTIPLIER, Double.toString(STAMP_COUNT_MULTIPLIER))));
      setStampsPerGroup(Integer.parseInt(properties.getProperty(ID_STAMPS_PER_GROUP, Integer.toString(STAMPS_PER_GROUP))));
      setStampGroupsCount(Integer.parseInt(properties.getProperty(ID_STAMP_GROUPS_COUNT, Integer.toString(STAMP_GROUPS_COUNT))));
    } catch (Exception e) {
      log.error("Invalid values in " + PREF_FILE_NAME);
      setNumberOfColors(NUMBER_OF_COLORS);
      setStampGroupsCount(STAMP_GROUPS_COUNT);
      setStampsPerGroup(STAMPS_PER_GROUP);
      setStampCountMultiplier(STAMP_COUNT_MULTIPLIER);
    }
  }
}
