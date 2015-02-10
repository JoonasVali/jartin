package ee.joonasvali.stamps.stamp;

/**
 * @author Joonas Vali
 */
public class StampGroupMetadata {
  private volatile double rarity = 1;

  public double getRarity() {
    return rarity;
  }

  public void setRarity(double rarity) {
    this.rarity = rarity;
  }
}
