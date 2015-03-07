package ee.joonasvali.stamps.ui;

import java.util.function.Supplier;

/**
 * @author Joonas Vali
 */
public class IntegerValidator implements Validator{
  private final int minValue;
  private final int maxValue;
  private final String name;
  private final Supplier<Integer> input;

  public IntegerValidator(Supplier<Integer> getter, int minValue, int maxValue, String name) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.name = name;
    this.input = getter;
  }

  @Override
  public String validate() {
    int val;
    try {
      val = input.get();
    } catch (Exception e) {
      return name + " has an invalid value: '" + input.get() + "'";
    }
    if (minValue > val) {
      return name + " value " + val + " can't be smaller than " + minValue;
    }

    if (maxValue < val) {
      return name + " value " + val + " can't be larger than " + maxValue;
    }
    return "";
  }
}
