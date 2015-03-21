package ee.joonasvali.stamps.query;

import java.util.HashSet;
import java.util.List;

/**
 * @author Joonas Vali
 *
 * Dynamically fills the ExcludingQuery list, so if something doesn't fit criteria, it will be excluded
 */
public class DynamicExcludingQuery<T> extends ExcludingQuery<T> {

  DynamicExcludingQueryCondition<T> condition;

  public DynamicExcludingQuery(Query<T> stampQuery, DynamicExcludingQueryCondition<T> condition) {
    super(stampQuery, new HashSet<>());
    this.condition = condition;
  }

  @Override
  public T get(List<T> list) {
    T ob;
    boolean isAlreadyExcluded;
    do {
      ob = super.get(list);
      boolean valid = condition.check(ob);
      if (!valid) {
        isAlreadyExcluded = !addExclusion(ob);
      } else {
        // This object fits criteria
        return ob;
      }
    } while (!isAlreadyExcluded);
    // if we got something that was already excluded, then most likely everything is excluded, we just return the element.
    return ob;
  }



}
