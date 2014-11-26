package ee.joonasvali.stamps;

/**
 * @author Joonas Vali
 */
public interface StampProvider {
  public Stamp getStamp(Query<Stamp> q);
}
