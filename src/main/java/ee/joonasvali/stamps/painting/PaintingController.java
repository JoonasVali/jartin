package ee.joonasvali.stamps.painting;

import ee.joonasvali.stamps.ui.Preferences;
import ee.joonasvali.stamps.ui.ProgressListener;

import java.awt.image.BufferedImage;
import java.util.Optional;

public interface PaintingController {
  void setRetainColors(boolean retainColors);

  void setRetainStamps(boolean retainStamps);

  void setRetainSpine(boolean retainSpine);

  void clearCaches();

  Optional<BufferedImage> generateImage(ProgressListener listener);

  Preferences getPrefs();
}
