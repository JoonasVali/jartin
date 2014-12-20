package ee.joonasvali.stamps.query;

import java.util.List;

/**
 * @author Joonas Vali
 */
public class XYFormulaQuery<C> extends PositionAwareQuery<C> {
  private Query<C> query;
  private BinaryQuery<C> binaryQuery;
  private BinaryFormula formula;

  public XYFormulaQuery(Query<C> query, BinaryQuery<C> binaryQuery, BinaryFormula formula) {
    this.query = query;
    this.binaryQuery = binaryQuery;
    this.formula = formula;
  }

  @Override
  protected C getUsingPosition(List<C> list) {
    return binaryQuery.get(list, formula.get(x, y), query);
  }

  public BinaryFormula getFormula() {
    return formula;
  }

  /**
   * Formula to calculate Y from X
   * @param x param
   * @return y param
   */


}
