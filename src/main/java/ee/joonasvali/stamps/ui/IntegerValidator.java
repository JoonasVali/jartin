package ee.joonasvali.stamps.ui;

import javax.swing.*;

/**
 * @author Joonas Vali
 */
public class IntegerValidator implements Validator{
  private int minValue;
  private int maxValue;
  private String name;
  private JTextField input;

  public IntegerValidator(JTextField input, int minValue, int maxValue, String name) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.name = name;
    this.input = input;
  }

  @Override
  public String validate() {
    int val;
    try {
      val = Integer.parseInt(input.getText());
    } catch (Exception e) {
      return name + " has an invalid value: '" + input.getText() + "'";
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
