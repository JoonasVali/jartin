package ee.joonasvali.stamps;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class StampPallette {
  private List<Stamp> stampsList;

  public StampPallette(List<Stamp> stampsList) {
    this.stampsList = new ArrayList<>(stampsList);
  }

  public StampPallette(StampProvider stamps, int count) {
    this.stampsList = new ArrayList<>(count);
    RandomQuery<Stamp> stampRandomQuery = new RandomQuery<>();
    for (int i = 0; i < count; i++) {
      stampsList.add(stamps.getStamp(stampRandomQuery));
    }
  }

  public Stamp getStamp(Query<Stamp> q) {
    return q.get(stampsList);
  }
}
