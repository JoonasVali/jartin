package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.query.Query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public interface StampProvider {
  public Stamp getStamp(Query<Stamp> q);
  List<Stamp> getStamps();
}
