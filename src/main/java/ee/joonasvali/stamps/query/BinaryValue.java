package ee.joonasvali.stamps.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Joonas Vali
 */
public enum BinaryValue {
  ZERO, ONE;

  private static List<BinaryValue> values;
  static {
    values = new ArrayList<>();
    values.add(ZERO);
    values.add(ONE);
    values = Collections.unmodifiableList(values);
  }

  public static BinaryValue get(Query<BinaryValue> query) {
    return query.get(values);
  }
}
