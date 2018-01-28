/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.properties;

import ee.joonasvali.stamps.code.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Joonas Vali
 */
public class AppProperties {
  private static Logger logger = LoggerFactory.getLogger(AppProperties.class);
  private volatile String outputPath;
  private volatile String stampsDirPath;
  private boolean lazyLoading;
  private volatile File stampsDir;

  private final static AppProperties properties = new AppProperties();

  public static AppProperties getInstance() {
    return properties;
  }

  private AppProperties() {
    loadString("jartin.stamps", this::setStampsDirPath);
    loadString("jartin.output", this::setOutput);
    loadBoolean("jartin.stamps.lazyloading", this::setLazyLoading);

    if (outputPath == null) {
      outputPath = System.getProperty("user.home") + File.separator + "jartin" + File.separator + "out";
    }

    File file = new File(outputPath);
    if (file.isFile()) {
      logger.error("Output path '" + file + "' must be a folder!");
      System.exit(-1);
    }

    if (!file.exists()) {
      file.mkdirs();
    }

    try {
      if (stampsDirPath != null) {
        stampsDir = new File(stampsDirPath).getCanonicalFile();
      } else {
        stampsDir = new File(Util.getUserDir(), "stamps").getCanonicalFile();
      }
    } catch (IOException ex) {
      logger.error("Unable to convert stamps directory to canonical path.", ex);
      logger.error("stampsDirPath = " + stampsDirPath);
      logger.error("user dir / stamps = " +  new File(Util.getUserDir(), "stamps"));
    }

    if (!stampsDir.exists()) {
      logger.error("Stampdir '" + stampsDir + "' must be existing folder!");
      System.exit(-1);
    }

    logger.info("Stamps loaded from " + stampsDir);
    logger.info("Jartin output is " + outputPath);
    logger.info("Using lazy loading for stamps: " + lazyLoading);
  }

  private void loadBoolean(String key, Consumer<Boolean> setter) {
    Boolean value = Boolean.parseBoolean(System.getProperty(key));

    setter.accept(value);
  }

  private void loadString(String key, Consumer<String> setter) {
    String value = System.getProperty(key);
    setter.accept(value);
  }

  private void setOutput(String outputPath) {
    this.outputPath = outputPath;
  }

  public String getOutput() {
    return outputPath;
  }

  public File getStampsDir() {
    return stampsDir;
  }

  private void setStampsDirPath(String stampsDirPath) {
    this.stampsDirPath = stampsDirPath;
  }

  public boolean isLazyLoading() {
    return lazyLoading;
  }

  public void setLazyLoading(boolean lazyLoading) {
    this.lazyLoading = lazyLoading;
  }
}

