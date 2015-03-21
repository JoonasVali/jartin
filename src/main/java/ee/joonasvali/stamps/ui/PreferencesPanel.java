package ee.joonasvali.stamps.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Joonas Vali
 */
public class PreferencesPanel extends JPanel {
  public static final int MAX_STAMP_DEMULTIPLIER = 20000;
  public static final int MIN_STAMP_DEMULTIPLIER = 100;
  private final Preferences preferences;
  private final JButton save = new JButton("Save");
  private final ActionListener closeAction;

  private final JLabel errors = new JLabel();
  private final List<Validator> validatorList = new LinkedList<>();
  private final List<ActionListener> onSave = new LinkedList<>();

  public PreferencesPanel(Preferences preferences, ActionListener closeAction) {
    this.closeAction = closeAction;
    this.preferences = preferences;
    init();
  }

  public void init() {
    this.setLayout(new GridLayout(0, 1));
    this.add(errors);
    create("Width", preferences::getWidth, preferences::setWidth, 100, Integer.MAX_VALUE);
    create("Height", preferences::getHeight, preferences::setHeight, 100, Integer.MAX_VALUE);
    create("Base colors", preferences::getNumberOfColors, preferences::setNumberOfColors, 2, 50);
    createSlider("Stamp amount", preferences::getStampCountDemultiplier, preferences::setStampCountDemultiplier,
        getDemultiplierDictionary(MIN_STAMP_DEMULTIPLIER, MAX_STAMP_DEMULTIPLIER), true,  MIN_STAMP_DEMULTIPLIER, MAX_STAMP_DEMULTIPLIER);
    create("Stamp groups count", preferences::getStampGroupsCount, preferences::setStampGroupsCount, 1, 100);
    create("Stamps per group", preferences::getStampsPerGroup, preferences::setStampsPerGroup, 1, 100);

    save.addActionListener(s -> {
      String errors = validateFields();
      if (errors.isEmpty()) {
        closeAction.actionPerformed(null);
        for (ActionListener listener : onSave) {
          listener.actionPerformed(s);
        }
      } else {
        this.errors.setForeground(Color.RED);
        this.errors.setText(errors);
      }
    });

    JPanel savePanel = new JPanel(new FlowLayout());
    savePanel.add(save);
    this.add(savePanel);
  }

  private void createSlider(String name, Supplier<Integer> supplier, Consumer<Integer> consumer, Dictionary dictionary, boolean inverted, int minValue, int maxValue) {
    JComponent component = createSliderObject(name, supplier, consumer, dictionary, inverted, minValue, maxValue);
    this.add(component);
  }

  private JComponent createSliderObject(String name, Supplier<Integer> supplier, Consumer<Integer> consumer, Dictionary dictionary, boolean inverted, int minValue, int maxValue) {
    JSlider slider = new JSlider(minValue, maxValue, supplier.get());
    slider.setInverted(inverted);
    if (dictionary != null) {
      slider.setLabelTable(dictionary);
      slider.setPaintLabels(true);
    }
    validatorList.add(new IntegerValidator(slider::getValue, minValue, maxValue, name));

    JPanel panel = new JPanel(new FlowLayout());
    panel.add(new JLabel(name));
    panel.add(slider);
    onSave.add(s -> {
      try {
        consumer.accept(slider.getValue());
      } catch (Exception e) {
        errors.setText(errors.getText() + "\n" + name + " " + e.getMessage());
      }
    });
    return panel;
  }

  private void create(String name, Supplier<Integer> supplier, Consumer<Integer> consumer, int minValue, int maxValue) {
    JComponent component = createInputField(name, supplier, consumer, minValue, maxValue);
    this.add(component);
  }

  private String validateFields() {
    for (Validator validator : validatorList) {
      String error = validator.validate();
      if (!error.isEmpty()) {
        return error;
      }
    }
    return "";
  }

  private JComponent createInputField(String name, Supplier<Integer> supplier, Consumer<Integer> consumer, int minValue, int maxValue) {
    JTextField field = new JTextField(String.valueOf(supplier.get()), 5);
    validatorList.add(new IntegerValidator(() -> Integer.parseInt(field.getText()), minValue, maxValue, name));

    JPanel panel = new JPanel(new FlowLayout());
    panel.add(new JLabel(name));
    panel.add(field);
    onSave.add(s -> {
      try {
        consumer.accept(Integer.parseInt(field.getText()));
      } catch (Exception e) {
        errors.setText(errors.getText() + "\n" + name + " " + e.getMessage());
      }
    });
    return panel;
  }

  public Hashtable<Integer, JLabel> getDemultiplierDictionary(int min, int max) {
    Hashtable<Integer, JLabel> table = new Hashtable<>();
    table.put(min, new JLabel("more"));
    table.put(max, new JLabel("less"));
    return table;
  }
}
