package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public interface Query<T> {
  /**
   *
   * @param list the list of candidates
   * @return always return one of the elements from the list.
   * @throws RuntimeException if list is empty
   */
  public T get(List<T> list);
}
