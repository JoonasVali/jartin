package ee.joonasvali.stamps.ui;

/**
 * @author Joonas Vali
 */
public class Preferences {
  public static final int WIDTH = 1800;
  public static final int HEIGHT = 1000;
  public static final int NUMBER_OF_COLORS = 3;
  public static final int STAMP_COUNT_DEMULTIPLIER = 1700;
  public static final int STAMP_GROUPS_COUNT = 4;
  public static final int STAMPS_PER_GROUP = 15;

  private int width = WIDTH;
  private int height = HEIGHT;
  private int numberOfColors = NUMBER_OF_COLORS;
  private int stampCountDemultiplier = STAMP_COUNT_DEMULTIPLIER;
  private int stampGroupsCount = STAMP_GROUPS_COUNT;
  private int stampsPerGroup = STAMPS_PER_GROUP;

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
}
