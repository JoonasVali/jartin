package ee.joonasvali.stamps.query;

/**
 * @author Joonas Vali
 */
public interface DynamicExcludingQueryCondition<T> {
  boolean check(T ob);
}
