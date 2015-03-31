/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.properties.MetadataReader;
import ee.joonasvali.stamps.query.Query;
import ee.joonasvali.stamps.query.RandomQuery;
import ee.joonasvali.stamps.ui.ProgressCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Joonas Vali
 */
public class StampLoader {
  public static final Logger log = LoggerFactory.getLogger(StampLoader.class);
  public static final String STAMPS_PROPERTIES = "stamps.properties";
  private final ExecutorService bootExecutor = Executors.newSingleThreadExecutor();

  private final File mainfolder;
  private final ArrayList<Stamps> stampsGroups;
  private volatile boolean loaded = false;

  public StampLoader(File mainfolder) {
    if (!mainfolder.exists() || !mainfolder.isDirectory())
      throw new IllegalArgumentException("Folder " + mainfolder + " must be dir");
    this.mainfolder = mainfolder;
    this.stampsGroups = new ArrayList<>();
  }

  public void loadStampsConcurrently() {
    if (!bootExecutor.isShutdown()) {
      bootExecutor.execute(() -> {
        loadStamps();
        bootExecutor.shutdown();
      });
    }
  }

  private synchronized void loadStamps() {
    if (loaded) {
      return;
    }
    long begin = System.currentTimeMillis();
    log.info("Starting to load stamps.");
    MetadataReader reader = new MetadataReader();
    File[] files = mainfolder.listFiles((dir, name) -> dir.isDirectory());
    stampsGroups.ensureCapacity(files.length);
    for (File dir : files) {
      Stamps stamps = new Stamps(dir);
      stampsGroups.add(stamps);

      File props = new File(dir, STAMPS_PROPERTIES);
      if (props.isFile()) {
        try {
          StampGroupMetadata metadata = reader.loadMetadata(props);
          stamps.setMetadata(metadata);
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
      }
    }
    loaded = true;
    long time = System.currentTimeMillis() - begin;
    log.info("Stamps loaded in " + time + "ms.");
  }

  /**
   * @param groups
   * @param stampsPerGroup
   * @param groupQuery
   * @param stampQuery
   * @param fillGroups
   * @return
   */
  public Stamps getStamps(int groups, int stampsPerGroup, Query<Stamps> groupQuery, Query<Stamp> stampQuery, boolean fillGroups, ProgressCounter listener) {
    init(listener);
    boolean remove = true;
    if (groupQuery instanceof RandomQuery) {
      groupQuery = getRandomRemovingQuery();
      remove = false;
    }
    LinkedList<Stamps> copy = new LinkedList<>(stampsGroups);
    List<Stamps> picked = new ArrayList<>(groups);
    for (int i = 0; i < groups; i++) {
      if (copy.isEmpty()) {
        log.error("Ran out of stamp groups at index " + (i + 1) + ", before could pick " + groups);
        return flatten(picked, stampsPerGroup, stampQuery, fillGroups);
      }

      Stamps s = groupQuery.get(copy);
      if (remove) {
        copy.remove(s);
      }
      picked.add(s);
    }
    return flatten(picked, stampsPerGroup, stampQuery, fillGroups);
  }

  private void init(ProgressCounter listener) {
    if (!loaded) {
      if (listener != null) {
        listener.setValue("Loading stamps...");
      }
      loadStamps();

      if (listener != null) {
        listener.clear();
      }
    }
  }

  private Stamps flatten(List<Stamps> picked, int stampsPerGroup, Query<Stamp> stampQuery, boolean fillGroups) {
    List<Stamp> flat = new ArrayList<>(100);
    picked.stream().forEach(s -> flat.addAll(getStamps(s.getStamps(), stampsPerGroup, stampQuery, fillGroups)));
    return new Stamps(flat);
  }

  private Collection<? extends Stamp> getStamps(List<Stamp> stamps, int stampsPerGroup, Query<Stamp> stampQuery, boolean fillGroups) {
    if (!fillGroups) {
      if (stampsPerGroup >= stamps.size()) {
        return stamps;
      }
    }
    List<Stamp> result = new ArrayList<>(stampsPerGroup);
    for (int i = 0; i < stampsPerGroup; i++) {
      result.add(stampQuery.get(stamps));
    }
    return result;
  }


  private Query<Stamps> getRandomRemovingQuery() {
    return list -> {
      int i = (int) (Math.random() * list.size());
      return list.remove(i);
    };
  }

  public void clearCaches() {
    init(null);
    stampsGroups.forEach(s -> s.getStamps().forEach(Stamp::clearRenderCache));
  }
}
