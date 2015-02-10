package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.query.Query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class CompositeStamps implements StampProvider {
  private final List<Stamp> list;

  public CompositeStamps(StampProvider stamps, StampComposerStrategy strategy) {
    list = stamps.getStamps();
    Stamps composites = strategy.compose(stamps);
    list.addAll(composites.getStamps());
  }

  @Override
  public Stamp getStamp(Query<Stamp> q) {
    return q.get(list);
  }

  @Override
  public List<Stamp> getStamps() {
    return list;
  }
}

