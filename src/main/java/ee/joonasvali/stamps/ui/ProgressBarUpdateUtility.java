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
  private volatile boolean queued = false;

  public ProgressBarUpdateUtility(JProgressBar bar) {
    this.bar = bar;
  }

  private void paintValue() {
    bar.setValue(value);
    bar.repaint();
    queued = false;
  }

  @Override
  public synchronized void setValue(int value) {
    if (queued || this.value == value) {
      return;
    }
    this.value = value;
    queued = true;
    SwingUtilities.invokeLater(updater);
  }


}
