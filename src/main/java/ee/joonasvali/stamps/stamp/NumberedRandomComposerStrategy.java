package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.query.RandomQuery;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joonas Vali
 */
public abstract class NumberedRandomComposerStrategy implements StampComposerStrategy {
  private final int count;

  public NumberedRandomComposerStrategy(int count) {
    this.count = count;
  }

  @Override
  public Stamps compose(StampProvider stamps) {
    RandomQuery<Stamp> q = new RandomQuery<>();
    List<Stamp> composites = new ArrayList<>(count);
    for(int i = 0; i < count; i++) {
      Stamp s1 = stamps.getStamp(q);
      Stamp s2 = stamps.getStamp(q);
      try {
        composites.add(create(s1, s2));
      } catch (CompositeStampCreationException e) {
        continue;
      }
    }
    return new Stamps(composites);
  }

  protected abstract Stamp create(Stamp s1, Stamp s2) throws CompositeStampCreationException;


  protected BufferedImage createMaxBufferedImage(Stamp s1, Stamp s2) {
    BufferedImage image1 = s1.getImg();
    BufferedImage image2 = s2.getImg();

    int width = Math.max(image1.getWidth(), image2.getWidth() );
    int height = Math.max(image1.getHeight(), image2.getHeight() );
    int maxDimension = (int) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));

    return new BufferedImage(maxDimension, maxDimension, BufferedImage.TYPE_INT_RGB);
  }
}
