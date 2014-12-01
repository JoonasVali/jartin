package ee.joonasvali.stamps.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Joonas Vali
 */
public class PreferencesPanel extends JPanel {
  private Preferences preferences;
  private JButton save = new JButton("Save");
  private ActionListener closeAction;
  public PreferencesPanel(Preferences preferences, ActionListener closeAction) {
    this.closeAction = closeAction;
    this.preferences = preferences;
    init();
  }

  public void init() {
    this.setLayout(new GridLayout(0, 1));
    this.add(createInputField("Width", preferences::getWidth, preferences::setWidth));
    this.add(createInputField("Height", preferences::getHeight, preferences::setHeight));
    this.add(createInputField("Base colors", preferences::getNumberOfColors, preferences::setNumberOfColors));
    this.add(createInputField("Stamp count demultiplier", preferences::getStampCountDemultiplier, preferences::setStampCountDemultiplier));
    this.add(createInputField("Stamp groups count", preferences::getStampGroupsCount, preferences::setStampGroupsCount));
    this.add(createInputField("Stamps per group", preferences::getStampsPerGroup, preferences::setStampsPerGroup));
    save.addActionListener(s -> closeAction.actionPerformed(null));

    JPanel savePanel = new JPanel(new FlowLayout());
    savePanel.add(save);
    this.add(savePanel);
  }

  private JComponent createInputField(String name, Supplier<Integer> supplier, Consumer<Integer> consumer) {
    JTextField field = new JTextField(String.valueOf(supplier.get()), 5);
    JPanel panel = new JPanel(new FlowLayout());
    panel.add(new JLabel(name));
    panel.add(field);
    save.addActionListener(s -> consumer.accept(Integer.parseInt(field.getText())));
    return panel;
  }


}
