package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public abstract class XYFormulaQuery<C> extends PositionAwareQuery<C> {
  private Query<C> query;
  private BinaryQuery<C> binaryQuery;

  public XYFormulaQuery(Query<C> query, BinaryQuery<C> binaryQuery) {
    this.query = query;
    this.binaryQuery = binaryQuery;
  }

  @Override
  protected C getUsingPosition(List<C> list) {
    double horison = get(x);
    BinaryValue over = y > horison ? BinaryValue.ONE : BinaryValue.ZERO;
    return binaryQuery.get(list, over, query);
  }

  /**
   * Formula to calculate Y from X
   * @param x param
   * @return y param
   */
  protected abstract double get(int x);


}
