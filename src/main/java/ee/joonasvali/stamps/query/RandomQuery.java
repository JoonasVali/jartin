package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class RandomQuery<T> implements Query<T> {

  public static <A> RandomQuery<A> create() {
    return new RandomQuery<>();
  }

  @Override
  public T get(List<T> list) {
    if (list.isEmpty()) throw new RuntimeException("provided list empty");
    return list.get((int) (Math.random() * list.size()));
  }
}
