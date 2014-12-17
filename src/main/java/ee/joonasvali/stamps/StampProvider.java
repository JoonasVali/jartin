package ee.joonasvali.stamps;

import ee.joonasvali.stamps.query.Query;

/**
 * @author Joonas Vali
 */
public interface StampProvider {
  public Stamp getStamp(Query<Stamp> q);
}
