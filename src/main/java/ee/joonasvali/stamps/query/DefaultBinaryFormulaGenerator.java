package ee.joonasvali.stamps.query;

import ee.joonasvali.stamps.ui.Preferences;

/**
 * @author Joonas Vali
 *
 * TODO resolve those magic numbers
 */
public class DefaultBinaryFormulaGenerator implements BinaryFormulaGenerator {
  @Override
  public BinaryFormula generate(Preferences prefs) {
    int waves = (int) (Math.random() * 3 + 1);
    BinaryFormula[] formulas = new BinaryFormula[waves];

    for (int i = 0; i < waves; i++) {
      double wavelength = Math.random() * (prefs.getWidth() / 400) + (prefs.getWidth() / 800);
      double offset = Math.random() * Math.PI;
      int movement = prefs.getHeight() / 2;
      int n = (int) ((Math.random() * (prefs.getHeight() - movement)) + movement) - prefs.getHeight() / 4;

      int slope = (int) (Math.random() * 200);

      formulas[i] =
          (x, y) -> {
            double s = Math.sin(Math.toRadians(x / wavelength) + offset) * slope + n;
            return y > s ? BinaryValue.ONE : BinaryValue.ZERO;
          };

    }
    if (waves > 1) {
      return new ReversingCompoundBinaryFormula(formulas);
    } else {
      return formulas[0];
    }
  }
}
