package ee.joonasvali.stamps.query;

import java.util.Arrays;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class ReversingCompoundBinaryFormula implements BinaryFormula {

  private List<BinaryFormula> formulas;

  public ReversingCompoundBinaryFormula(BinaryFormula... formulas) {
    this.formulas = Arrays.asList(formulas);
  }

  @Override
  public BinaryValue get(int x, int y) {
    boolean flip = false;
    for(BinaryFormula f : formulas) {
      if (f.get(x, y).getBooleanValue()) {
        flip = !flip;
      }
    }
    return flip ? BinaryValue.ONE : BinaryValue.ZERO;
  }
}
