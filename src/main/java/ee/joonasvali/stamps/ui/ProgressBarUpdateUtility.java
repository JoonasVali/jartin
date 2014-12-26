package ee.joonasvali.stamps.ui;

import javax.swing.*;

/**
 * @author Joonas Vali
 */
public class ProgressBarUpdateUtility implements ProgressListener {
  private final JProgressBar bar;
  private volatile int value;
  private Runnable updater = new Runnable() {
    @Override
    public void run() {
      paintValue();
    }
  };

  public ProgressBarUpdateUtility(JProgressBar bar) {
    this.bar = bar;
  }

  private void paintValue() {
    bar.setValue(value);
    bar.repaint();
  }

  @Override
  public synchronized void setValue(int value) {
    if (this.value == value) {
      return;
    }
    this.value = value;
    SwingUtilities.invokeLater(updater);
  }


}
