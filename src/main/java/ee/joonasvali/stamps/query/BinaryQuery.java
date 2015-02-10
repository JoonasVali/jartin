package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class BinaryQuery<T> {
  private final double division;
  /**
   * @param division 0 < x < 1
   */
  public BinaryQuery(double division) {
    if(division <= 0 || division >= 1) {
      throw new IllegalArgumentException("Division can't be " + division + " must be between 0 and 1");
    }
    this.division = division;
  }

  public T get(List<T> list, BinaryValue binaryValue, Query<T> query) {
    int posSize = (int) (list.size() * division);
    int size = 0;
    int startPos = 0;
    if (BinaryValue.ZERO.equals(binaryValue)) {
      size = posSize;
      startPos = 0;
      size = Math.max(size, 1);
    } else {
      posSize = Math.max(posSize, 1);
      size = list.size() - posSize;
      startPos = posSize;
    }
    return query.get(new DelegatorList<>(list, startPos, startPos + size));
  }
}
