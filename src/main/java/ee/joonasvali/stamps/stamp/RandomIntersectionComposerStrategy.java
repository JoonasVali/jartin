package ee.joonasvali.stamps.stamp;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * @author Joonas Vali
 */
public class RandomIntersectionComposerStrategy extends NumberedRandomComposerStrategy {


  public RandomIntersectionComposerStrategy(int count) {
    super(count);
  }

  @Override
  public Stamp create(Stamp s1, Stamp s2) throws CompositeStampCreationException {
    BufferedImage image1 = s1.getImg();
    BufferedImage image2 = s2.getImg();

    BufferedImage image = fillWhite(createMaxBufferedImage(s1, s2));
    BufferedImage image1Transformed = fillWhite(createMaxBufferedImage(s1, s2));
    BufferedImage image2Transformed = fillWhite(createMaxBufferedImage(s1, s2));

    double rotation1 = Math.toRadians(Math.random() * 360);
    double rotation2 = Math.toRadians(Math.random() * 360);

    transform(image1, image1Transformed, rotation1, image.getWidth(), image.getHeight());
    transform(image2, image2Transformed, rotation2, image.getWidth(), image.getHeight());

    createIntersection(image1Transformed, image2Transformed, image);
    try {
      image = ImageUtil.trim(image, Color.WHITE);
    } catch(IllegalArgumentException e) {
      throw new CompositeStampCreationException(e);
    }

    return new Stamp(image);
  }


  private void transform(BufferedImage image, BufferedImage imageTransformed, double rotation, int totalWidth, int totalHeight) {
    Graphics2D g1 = (Graphics2D) imageTransformed.getGraphics();
    g1.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    AffineTransform tr1 = new AffineTransform();
    tr1.setToTranslation(totalWidth / 2d - image.getWidth() / 2d, totalHeight / 2d -  image.getHeight() / 2d);
    tr1.rotate(rotation, image.getWidth() / 2, image.getHeight() / 2);
    g1.drawImage(image, tr1, null);
  }

  private void createIntersection(BufferedImage image1, BufferedImage image2, BufferedImage image) {
    int WHITE = Color.WHITE.getRGB();
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        if (isValid(x, y, image1) && isValid(x, y, image2)) {

          int color1 = image1.getRGB(x, y);
          int color2 = image2.getRGB(x, y);
          if (color1 == WHITE || color2 == WHITE) {
            continue;
          }
          int minColor = Math.max(color1, color2);
          image.setRGB(x, y, minColor);
        }
      }
    }
  }

  private BufferedImage fillWhite(BufferedImage img) {
    for(int i = 0; i < img.getWidth(); i++) {
      for(int j = 0; j < img.getWidth(); j++) {
        img.setRGB(i, j, Color.WHITE.getRGB());
      }
    }
    return img;
  }

  private boolean isValid(int x, int y, BufferedImage image) {
    return (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight());
  }
}
