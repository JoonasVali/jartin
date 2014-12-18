package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class XYFormulaQuery<C> extends PositionAwareQuery<C> {
  private Query<C> query;
  private BinaryQuery<C> binaryQuery;
  private Formula formula;

  public XYFormulaQuery(Query<C> query, BinaryQuery<C> binaryQuery, Formula formula) {
    this.query = query;
    this.binaryQuery = binaryQuery;
    this.formula = formula;
  }

  @Override
  protected C getUsingPosition(List<C> list) {
    double horison = formula.get(x);
    BinaryValue over = y > horison ? BinaryValue.ONE : BinaryValue.ZERO;
    return binaryQuery.get(list, over, query);
  }

  /**
   * Formula to calculate Y from X
   * @param x param
   * @return y param
   */


}
