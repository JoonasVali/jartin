package ee.joonasvali.stamps;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class CompositeStamps implements StampProvider {
  private List<Stamp> list;

  public CompositeStamps(Stamps stamps, StampComposerStrategy strategy) {
    list = stamps.getStamps();
    Stamps composites = strategy.compose(stamps);
    list.addAll(composites.getStamps());
  }

  @Override
  public Stamp getStamp(Query<Stamp> q) {
    return q.get(list);
  }
}

