package ee.joonasvali.stamps.stamp;

/**
 * @author Joonas Vali
 */
public interface StampComposerStrategy {
  public Stamps compose(StampProvider stamps);
}
