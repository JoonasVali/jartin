package ee.joonasvali.stamps;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DefaultProjectionFactory implements ProjectionFactory {
  @Override
  public BufferedImage getRawProjection(BufferedImage img, Color color) {
    BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for (int i = 0; i < img.getHeight(); i++) {
      for (int j = 0; j < img.getWidth(); j++) {
        int rgb = img.getRGB(j, i);
        int red = (rgb >> 16) & 0x000000FF;
        int green = (rgb >> 8) & 0x000000FF;
        int blue = (rgb) & 0x000000FF;
        if (red == 255 && green == 255 && blue == 255) {
          // Transparent
          newImg.setRGB(j, i, 0);
        } else {
          int alpha = 255 - (red + green + blue) / 3;
          Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
          newImg.setRGB(j, i, c.getRGB());
        }
      }
    }
    return newImg;
  }

  @Override
  public Projection getProjectionFromRaw(BufferedImage image) {
    return new DefaultProjection(image);
  }
}
