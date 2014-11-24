package ee.joonasvali.stamps;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class RandomQuery<T> implements Query<T> {
  @Override
  public T get(List<T> list) {
    return list.get((int) (Math.random() * list.size()));
  }
}
