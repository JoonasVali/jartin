package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public interface Query<T> {
  public T get(List<T> list);
}
