/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.properties;

import ee.joonasvali.stamps.stamp.StampGroupMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author Joonas Vali
 */
public class MetadataReader {

  public StampGroupMetadata loadMetadata(File props) throws IOException {
    Properties properties = new Properties();
    properties.load(new FileInputStream(props));
    StampGroupMetadata metadata = new StampGroupMetadata();

    loadDouble(properties, metadata, "rarity", 1, metadata::setRarity);

    return metadata;
  }

  private void loadDouble(Properties properties, StampGroupMetadata metadata, String name, double defaultValue, Consumer<Double> action) {
    double rarity;
    try {
      rarity = Double.parseDouble((String) properties.getOrDefault(name, defaultValue));
    } catch(Exception e) {
      rarity = defaultValue;
    }
    action.accept(rarity);
  }
}
