package ee.joonasvali.stamps.painting;

import ee.joonasvali.stamps.ui.Preferences;
import ee.joonasvali.stamps.ui.ProgressListener;

import java.awt.image.BufferedImage;

public interface PaintingController {
  void setRetainColors(boolean retainColors);

  void setRetainStamps(boolean retainStamps);

  void setRetainSpine(boolean retainSpine);

  void clearCaches();

  BufferedImage generateImage(ProgressListener listener);

  Preferences getPrefs();
}
