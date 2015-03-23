package ee.joonasvali.stamps.ui;

import javax.swing.*;

/**
 * @author Joonas Vali
 */
public class ProgressBarUpdateUtility implements ProgressListener {
  private final JProgressBar bar;
  private volatile int value;
  private volatile boolean queued = false;
  private volatile String message;

  public ProgressBarUpdateUtility(JProgressBar bar) {
    this.bar = bar;
  }

  private void paintValue() {
    String mess = message;
    if (mess != null) {
      bar.setString(mess);
    } else {
      bar.setString(null);
      bar.setValue(value);
    }
    bar.repaint();
    queued = false;
  }

  @Override
  public synchronized void setValue(int value) {
    if ((queued || this.value == value) && message == null) {
      return;
    }
    message = null;
    this.value = value;
    queued = true;
    SwingUtilities.invokeLater(this::paintValue);
  }

  @Override
  public void setValue(String message) {
    this.message = message;
    queued = false;
    SwingUtilities.invokeLater(this::paintValue);
  }


}
