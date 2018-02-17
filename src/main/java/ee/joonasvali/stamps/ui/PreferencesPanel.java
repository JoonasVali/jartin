/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger log = LoggerFactory.getLogger(PreferencesPanel.class);
  public static final int MAX_STAMP_MULTIPLIER = 100;
  public static final int MIN_STAMP_MULTIPLIER = 1;
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
    createSlider("Stamp amount", preferences::getStampCountMultiplier, preferences::setStampCountMultiplier,
        getMultiplierDictionary(MIN_STAMP_MULTIPLIER, MAX_STAMP_MULTIPLIER), false, MIN_STAMP_MULTIPLIER, MAX_STAMP_MULTIPLIER);
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

  private void createSlider(String name, Supplier<Double> supplier, Consumer<Double> consumer, Dictionary dictionary, boolean inverted, int minValue, int maxValue) {
    JComponent component = createSliderObject(name, supplier, consumer, dictionary, inverted, minValue, maxValue);
    this.add(component);
  }

  private JComponent createSliderObject(String name, Supplier<Double> supplier, Consumer<Double> consumer, Dictionary dictionary, boolean inverted, int minValue, int maxValue) {
    JSlider slider = new JSlider(minValue, maxValue, doubleToInt(supplier.get()));
    slider.setInverted(inverted);
    if (dictionary != null) {
      slider.setLabelTable(dictionary);
      slider.setPaintLabels(true);
    }
    validatorList.add(new NumericValidator(slider::getValue, minValue, maxValue, name));

    JPanel panel = new JPanel(new FlowLayout());
    panel.add(new JLabel(name));
    panel.add(slider);
    onSave.add(s -> {
      try {
        log.info("slider value: " + slider.getValue());
        consumer.accept(doubleFromInt(slider.getValue()));
      } catch (Exception e) {
        errors.setText(errors.getText() + "\n" + name + " " + e.getMessage());
      }
    });
    return panel;
  }

  private int doubleToInt(double value) {
    return (int)(value * 100);
  }

  private double doubleFromInt(int value) {
    return (double)value / 100d;
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
    validatorList.add(new NumericValidator(() -> Integer.parseInt(field.getText()), minValue, maxValue, name));

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

  public Hashtable<Integer, JLabel> getMultiplierDictionary(int min, int max) {
    Hashtable<Integer, JLabel> table = new Hashtable<>();
    table.put(min, new JLabel("less"));
    table.put(max, new JLabel("more"));
    return table;
  }
}
