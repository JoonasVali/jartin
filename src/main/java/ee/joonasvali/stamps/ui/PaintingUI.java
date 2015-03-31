/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.ui;

import ee.joonasvali.stamps.code.Util;
import ee.joonasvali.stamps.meta.Metadata;
import ee.joonasvali.stamps.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Joonas Vali
 */
public class PaintingUI extends JPanel {

  public static final int DATA_MARGIN_FROM_EDGE = 50;
  public static Logger log = LoggerFactory.getLogger(PaintingUI.class);

  private final ExecutorService generalGeneratorExecutor = Executors.newSingleThreadExecutor();
  private final ProgressListener progressListener;
  private final PaintingController controller;
  private volatile BufferedImage lastImage;
  private volatile Future executingTask;
  private volatile boolean isEmptyImage = true;

  public PaintingUI(PaintingController controller, ProgressListener listener) {
    this.progressListener = listener;
    this.controller = controller;
    initEmpty();
    this.add(new JLabel(new ImageIcon(lastImage)));
  }



  private void initEmpty() {
    log.debug("Start initializing PaintingUI");
    lastImage = new BufferedImage(getPrefs().getWidth(), getPrefs().getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D) lastImage.getGraphics();
    g.setColor(Color.LIGHT_GRAY);
    int max = (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024));
    String rating = "OK";
    if (max < 800) {
      rating = "low";
    } else if (max < 1000) {
      rating = "could use more";
    }

    Increaser inc = new Increaser(35, 15);
    int processors = Runtime.getRuntime().availableProcessors();
    g.drawString(Metadata.INSTANCE.getName() + " " + Metadata.INSTANCE.getVersion(), DATA_MARGIN_FROM_EDGE, inc.getNext());
    g.drawString("Image size set to " + getPrefs().getWidth() + " : " + getPrefs().getHeight(), DATA_MARGIN_FROM_EDGE, inc.getNext());
    g.drawString("Total memory available to Java VM: " + max + " MB " + "(" + rating + ")", DATA_MARGIN_FROM_EDGE, inc.getNext());
    g.drawString("OS Architecture: " + System.getProperty("os.arch"), DATA_MARGIN_FROM_EDGE, inc.getNext());
    g.drawString("JVM Bits: " + System.getProperty("sun.arch.data.model"), DATA_MARGIN_FROM_EDGE, inc.getNext());


    g.drawString("Number of processors available to Java VM: " + processors, DATA_MARGIN_FROM_EDGE, inc.getNext());
    if(AppProperties.getInstance().isLazyLoading()) {
      g.drawString("Using lazy loading for stamps (Slower but conserves memory).", DATA_MARGIN_FROM_EDGE, inc.getNext());
    }
    g.drawString("Press \"Generate\" to generate your first image.", DATA_MARGIN_FROM_EDGE, inc.getNext());
    log.debug("Stop initializing PaintingUI");
  }

  public BufferedImage getLastImage() {
    return lastImage;
  }

  public void setRetainColors(boolean retainColors) {
    controller.setRetainColors(retainColors);
  }

  public void setRetainStamps(boolean retainStamps) {
    controller.setRetainStamps(retainStamps);
  }

  public void setRetainSpine(boolean retainSpine) {
    controller.setRetainSpine(retainSpine);
  }

  public void generate(final Runnable after) {
    executingTask = generalGeneratorExecutor.submit(() -> {
      if (!isEmptyImage) {
        controller.clearCaches();
      }
      BufferedImage image =  controller.generateImage(progressListener);
      if (image != null) {
        lastImage = image;
        isEmptyImage = false;
      }
      SwingUtilities.invokeLater(after);
      executingTask = null;

    });
  }

  public void commitImage() {
    Util.assertEDT();
    PaintingUI.this.removeAll();
    PaintingUI.this.add(new JLabel(new ImageIcon(lastImage)));
  }

  public Preferences getPrefs() {
    return controller.getPrefs();
  }

  public boolean isExecuting() {
    return executingTask != null;
  }

  public void cancel(final Runnable after) {
    Future task = executingTask;
    if (task != null) {
      task.cancel(true);
      after.run();
    }
  }

  private class Increaser {
    private int count;
    private int increase;

    private Increaser(int initialCount, int increase) {
      this.count = initialCount;
      this.increase = increase;
    }

    public int getNext() {
      int val = count;
      count += increase;
      return count;
    }
  }
}
