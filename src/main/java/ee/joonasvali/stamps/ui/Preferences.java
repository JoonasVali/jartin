package ee.joonasvali.stamps.ui;

import java.awt.*;

/**
 * @author Joonas Vali
 */
public class Preferences {
  public static final int WIDTH = getMonitorWidth();
  public static final int HEIGHT = getMonitorHeight();
  public static final int NUMBER_OF_COLORS = 3;
  public static final int STAMP_COUNT_DEMULTIPLIER = 2500;
  public static final int STAMP_GROUPS_COUNT = 6;
  public static final int STAMPS_PER_GROUP = 20;
  public static final boolean SPINE_MODE = false;

  private int width = WIDTH;
  private int height = HEIGHT;
  private int numberOfColors = NUMBER_OF_COLORS;
  private int stampCountDemultiplier = STAMP_COUNT_DEMULTIPLIER;
  private int stampGroupsCount = STAMP_GROUPS_COUNT;
  private int stampsPerGroup = STAMPS_PER_GROUP;
  private boolean spineMode = SPINE_MODE;

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

  public int getStampCountDemultiplier() {
    return stampCountDemultiplier;
  }

  public void setStampCountDemultiplier(int stampCountDemultiplier) {
    this.stampCountDemultiplier = stampCountDemultiplier;
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
    int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    System.out.println("Resolution width detected: " + width + " px");
    return Math.max(width, 200);
  }

  private static int getMonitorHeight() {
    int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    System.out.println("Resolution height detected: " + height + " px");
    return Math.max(height, 200);
  }
}
