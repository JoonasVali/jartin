package ee.joonasvali.stamps.query;

import ee.joonasvali.stamps.ui.Preferences;

/**
 * @author Joonas Vali
 */
public interface BinaryFormulaGenerator {
  public BinaryFormula generate(Preferences preferences);
}
