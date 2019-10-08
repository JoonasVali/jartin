package ee.joonasvali.stamps.painting;

import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.Pallette;
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

  ColorModel getBackgroundColorModel();

  Pallette getPallette();

  void setBackgroundColorModel(ColorModel backgroundColorModel);

  void setPallette(Pallette pallette);

  Preferences getPrefs();
}
