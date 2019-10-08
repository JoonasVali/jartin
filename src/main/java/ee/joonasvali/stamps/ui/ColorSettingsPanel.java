package ee.joonasvali.stamps.ui;

import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.GradientColorModel;
import ee.joonasvali.stamps.color.Pallette;
import ee.joonasvali.stamps.color.PlainColorModel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorSettingsPanel extends JPanel {

  private final Supplier<Pallette> getPallette;
  private final Consumer<Pallette> setPallette;
  private final Supplier<ColorModel> getBackground;
  private final Consumer<ColorModel> setBackround;

  public ColorSettingsPanel(
      Supplier<Pallette> getPallette,
      Consumer<Pallette> setPallette,
      Supplier<ColorModel> getBackground,
      Consumer<ColorModel> setBackround) {
    this.getPallette = getPallette;
    this.setPallette = setPallette;
    this.getBackground = getBackground;
    this.setBackround = setBackround;
  }

  public void refresh() {
    Pallette pallette = getPallette.get();
    this.removeAll();
    setLayout(new GridLayout(0, 1));
    for (ColorModel model : pallette.getModels()) {
      JPanel panel = new JPanel();
      panel.setLayout(new FlowLayout());
      if (model instanceof GradientColorModel) {
        GradientColorModel gModel = (GradientColorModel) model;
        Color upperColor = gModel.getUpper();
        Color lowerColor = gModel.getLower();
        JLabel upperLabel = new JLabel("█");
        upperLabel.setForeground(upperColor);
        JLabel lowerLabel = new JLabel("█");
        lowerLabel.setForeground(lowerColor);
        panel.add(upperLabel);
        panel.add(lowerLabel);
      }
      else if (model instanceof PlainColorModel) {
        PlainColorModel pModel = (PlainColorModel) model;
        Color color = pModel.getColor(null);
        JLabel label = new JLabel("█");
        label.setForeground(color);
        panel.add(label);
      }
      this.add(panel);
      this.add(new JSeparator(JSeparator.HORIZONTAL));
    }
  }

  public void showInFrame() {
    JFrame frame = new JFrame();
    frame.add(ColorSettingsPanel.this);
    frame.setSize(new Dimension(300, 500));
    refresh();
    frame.setVisible(true);
  }
}
