package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public abstract class PositionAwareQuery<T> implements Query<T> {
  protected int x;
  protected int y;
  protected double size;
  protected double rotation;
  protected boolean argumentsSet = false;

  public void provide(int x, int y, double size, double rotation) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.rotation = rotation;
    argumentsSet = true;
  }

  @Override
  public T get(List<T> list) {
    if (!argumentsSet) throw new IllegalStateException("Must provide arguments first!");
    return getUsingPosition(list);
  }

  protected abstract T getUsingPosition(List<T> list);
}
