/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.painting;

import ee.joonasvali.stamps.Projection;
import ee.joonasvali.stamps.ProjectionGenerator;
import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.ColorUtil;
import ee.joonasvali.stamps.color.GradientColorModel;
import ee.joonasvali.stamps.color.Pallette;
import ee.joonasvali.stamps.color.PlainColorModel;
import ee.joonasvali.stamps.properties.AppProperties;
import ee.joonasvali.stamps.query.BinaryFormula;
import ee.joonasvali.stamps.query.BinaryFormulaGenerator;
import ee.joonasvali.stamps.query.BinaryQuery;
import ee.joonasvali.stamps.query.BinaryValue;
import ee.joonasvali.stamps.query.Query;
import ee.joonasvali.stamps.query.RandomQuery;
import ee.joonasvali.stamps.query.ReversingCompoundBinaryFormula;
import ee.joonasvali.stamps.query.XYFormulaQuery;
import ee.joonasvali.stamps.stamp.CompositeStamps;
import ee.joonasvali.stamps.stamp.RandomIntersectionComposerStrategy;
import ee.joonasvali.stamps.stamp.RandomMergeComposerStrategy;
import ee.joonasvali.stamps.stamp.Stamp;
import ee.joonasvali.stamps.stamp.StampLoader;
import ee.joonasvali.stamps.stamp.StampProvider;
import ee.joonasvali.stamps.stamp.Stamps;
import ee.joonasvali.stamps.ui.Preferences;
import ee.joonasvali.stamps.ui.ProgressCounter;
import ee.joonasvali.stamps.ui.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author Joonas Vali
 */
public final class PaintingControllerImpl implements PaintingController {

  public static final double CONSTANT_STAMP_COUNT_DIVIDER = 0.001d;
  private static Logger log = LoggerFactory.getLogger(PaintingControllerImpl.class);
  private static final double CHANCE_OF_GRADIENT_COLOR = 0.7;
  private static RandomQuery<ColorModel> backgroundColorModelChooser = RandomQuery.create();

  private final Preferences prefs = new Preferences();
  private final BinaryFormulaGenerator colorFormulaGenerator;
  private final BinaryFormulaGenerator stampFormulaGenerator;
  private final BinaryFormulaGenerator colorModelFormulaGenerator;

  private volatile boolean retainColors = false;
  private volatile boolean retainStamps = false;
  private volatile boolean retainSpine = false;

  private final StampLoader stampPool = new StampLoader(AppProperties.getInstance().getStampsDir());

  private volatile ColorModel backgroundColorModel;
  private volatile Pallette pallette;
  private volatile StampProvider stamps;

  private volatile Query<Stamp> stampQuery;
  private volatile Query<ColorModel> colorModelQuery;
  private volatile Query<Color> colorQuery;

  private final ProjectionRenderer projectionRenderer;

  public PaintingControllerImpl(BinaryFormulaGenerator colorModelFormulaGenerator, BinaryFormulaGenerator stampFormulaGenerator, BinaryFormulaGenerator colorFormulaGenerator) {
    this.colorModelFormulaGenerator = colorModelFormulaGenerator;
    this.stampFormulaGenerator = stampFormulaGenerator;
    this.colorFormulaGenerator = colorFormulaGenerator;
    this.projectionRenderer = new ProjectionRenderer();
    stampPool.loadStampsConcurrently();
  }

  @Override
  public void setRetainColors(boolean retainColors) {
    this.retainColors = retainColors;
  }

  @Override
  public void setRetainStamps(boolean retainStamps) {
    this.retainStamps = retainStamps;
  }

  @Override
  public void setRetainSpine(boolean retainSpine) {
    this.retainSpine = retainSpine;
  }

  @Override
  public void clearCaches() {
    log.info("Clearing Caches");
    Stamp.clearCache();
    stampPool.clearCaches();
  }

  /**
   * @return Image or null if cancelled
   */
  @Override
  public synchronized Optional<BufferedImage> generateImage(ProgressListener listener) {
    Backup backup = new Backup(pallette, stamps, stampQuery, colorModelQuery, colorQuery);

    long startTime = System.currentTimeMillis();
    log.info("Starting generating a new image");
    int x = prefs.getWidth();
    int y = prefs.getHeight();

    boolean showSpine = prefs.isSpineMode();
    int projections = 0;

    ProgressCounter counter = new ProgressCounter(listener, projections);
    stamps = createStamps(counter);

    if (!showSpine) {
      projections = ((int)(x * y * CONSTANT_STAMP_COUNT_DIVIDER * prefs.getStampCountMultiplier()));
      projections = adaptProjectionsToStampsSizes(projections, stamps);
      counter.setProjections(projections);
      log.info("Number of projections: " + projections);
    }

    if (pallette == null || !retainColors) {
      pallette = new Pallette(generateColorModels(new Random()));
      backgroundColorModel = pallette.getColor(backgroundColorModelChooser);
    } else {
      log.debug("Skip generating color models");
    }

    ProjectionGenerator gen = new ProjectionGenerator(x, y, stamps, pallette, new Random());


    if (stampQuery == null || colorModelQuery == null || colorQuery == null || !retainSpine) {
      stampQuery = generateXYFormulaQuery(stampFormulaGenerator);
      colorModelQuery = generateXYFormulaQuery(colorModelFormulaGenerator);
      colorQuery = generateXYFormulaQuery(colorFormulaGenerator);
    }

    if (!showSpine) {
      try {
        log.info("Start painting.");
        List<Projection> projectionList = projectionRenderer.render(() -> gen.generate(stampQuery, colorModelQuery, colorQuery), projections, counter);
        Painting painting = new Painting(x, y, backgroundColorModel, projectionList);

        BufferedImage result = painting.paint(counter);
        long endTime = System.currentTimeMillis();
        log.info("Generating new image completed. Total time: " + (endTime - startTime) + " ms");
        return Optional.of(result);
      }
      catch (InterruptedException e) {
        log.info("No image available. Painting cancelled.");
        counter.clear();
        backup.revert();
        return Optional.empty();
      }
    } else {
      log.warn("SPINE MODE!");
      return Optional.of(paintLines(colorModelQuery));
    }
  }

  private int adaptProjectionsToStampsSizes(int projections, StampProvider stamps) {
    int defaultSize = 150;
    int defArea = defaultSize * defaultSize;
    double projectionsTemp = projections;
    double affection = ((double)1 / (double)stamps.size()) * projections;
    for (Stamp stamp : stamps.getStamps()) {
      Point size = stamp.size();
      int area = size.x * size.y;
      double multiplier = (double)defArea / (double)area;
      double a = (multiplier - 1) * affection * stamp.getMetadata().getRarity();
      projectionsTemp += a;
    }
    // Just in case limit the number of projections to 3 times the original count.
    return (int) Math.min(projectionsTemp, projections * 3);
  }

  private StampProvider createStamps(ProgressCounter counter) {
    if (stamps == null || !retainStamps) {
      log.info("Generating stamps");
      Stamps loadedStamps = stampPool.getStamps(prefs.getStampGroupsCount(), prefs.getStampsPerGroup(), RandomQuery.create(), RandomQuery.create(), false, counter);
      // TODO, this composite thing should be more dynamic. (What's the number 10?)
      CompositeStamps compositeStamps = new CompositeStamps(loadedStamps, new RandomIntersectionComposerStrategy((int) (Math.random() * 10)));
      compositeStamps = new CompositeStamps(compositeStamps, new RandomMergeComposerStrategy((int) (Math.random() * 10)));
      return compositeStamps;
    } else {
      log.debug("Skip generating stamps");
      return stamps;
    }
  }


  private <T> Query<T> generateXYFormulaQuery(BinaryFormulaGenerator generator) {
    ReversingCompoundBinaryFormula formula = new ReversingCompoundBinaryFormula(generator.generate(prefs));
    return new XYFormulaQuery(new RandomQuery<T>(), new BinaryQuery<T>(Math.random()), formula);
  }

  private java.util.List<ColorModel> generateColorModels(Random random) {
    log.info("Generating color models");
    int colors = prefs.getNumberOfColors();
    java.util.List<ColorModel> colorModels = new ArrayList<>(colors);

    for (int i = 0; i < colors; i++) {
      if (Math.random() < CHANCE_OF_GRADIENT_COLOR) {
        colorModels.add(new GradientColorModel(ColorUtil.getRandomColor(random), ColorUtil.getRandomColor(random), prefs.getHeight(), prefs.getWidth()));
      } else {
        colorModels.add(new PlainColorModel(ColorUtil.getRandomColor(random)));
      }
    }
    return colorModels;
  }

  @Override
  public Preferences getPrefs() {
    return prefs;
  }

  private BufferedImage paintLines(Query<?> q) {
    BufferedImage image = new BufferedImage(getPrefs().getWidth(), getPrefs().getHeight(), BufferedImage.TYPE_INT_RGB);
    if (!(q instanceof XYFormulaQuery)) {
      log.error("Invalid query for paintLines");
      System.exit(-1);
    }

    BinaryFormula formula = ((XYFormulaQuery) q).getFormula();

    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        Color color = formula.get(i, j).equals(BinaryValue.ONE) ? Color.GRAY : Color.DARK_GRAY;
        image.setRGB(i, j, color.getRGB());
      }
    }

    return image;

  }


  private class Backup {
    private Pallette pallette;
    private StampProvider stamps;
    private Query<Stamp> stampQuery;
    private Query<ColorModel> colorModelQuery;
    private Query<Color> colorQuery;

    public Backup(Pallette pallette, StampProvider stamps, Query<Stamp> stampQuery, Query<ColorModel> colorModelQuery, Query<Color> colorQuery) {
      this.pallette = pallette;
      this.stamps = stamps;
      this.stampQuery = stampQuery;
      this.colorModelQuery = colorModelQuery;
      this.colorQuery = colorQuery;
    }

    private Pallette getPallette() {
      return pallette;
    }

    private StampProvider getStamps() {
      return stamps;
    }

    private Query<Stamp> getStampQuery() {
      return stampQuery;
    }

    private Query<ColorModel> getColorModelQuery() {
      return colorModelQuery;
    }

    private Query<Color> getColorQuery() {
      return colorQuery;
    }

    public void revert() {
      log.info("Reverting data from backup");
      PaintingControllerImpl.this.colorQuery = colorQuery;
      PaintingControllerImpl.this.stamps = stamps;
      PaintingControllerImpl.this.pallette = pallette;
      PaintingControllerImpl.this.colorModelQuery = colorModelQuery;
      PaintingControllerImpl.this.stampQuery = stampQuery;
    }
  }
}

