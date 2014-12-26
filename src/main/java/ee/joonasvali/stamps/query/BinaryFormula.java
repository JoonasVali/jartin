package ee.joonasvali.stamps.query;

/**
 * @author Joonas Vali
 */
public interface BinaryFormula {
  public abstract BinaryValue get(int x, int y);
}
