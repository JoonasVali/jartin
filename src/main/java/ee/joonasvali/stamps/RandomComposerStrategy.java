package ee.joonasvali.stamps;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class RandomComposerStrategy implements StampComposerStrategy {
  private int count;

  public RandomComposerStrategy(int count) {
    this.count = count;
  }

  @Override
  public Stamps compose(Stamps stamps) {
    RandomQuery<Stamp> q = new RandomQuery<>();
    List<Stamp> composites = new ArrayList<>(count);
    for(int i = 0; i < count; i++) {
      Stamp s1 = stamps.getStamp(q);
      Stamp s2 = stamps.getStamp(q);
      composites.add(create(s1, s2));
    }
    return new Stamps(composites);
  }

  private Stamp create(Stamp s1, Stamp s2) {
    BufferedImage image1 = s1.getImg();
    BufferedImage image2 = s2.getImg();

    int width = Math.max(image1.getWidth(), image2.getWidth() );
    int height = Math.max(image1.getHeight(), image2.getHeight() );
    int maxDimension = (int) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));

    BufferedImage image = new BufferedImage(maxDimension, maxDimension, BufferedImage.TYPE_INT_RGB);
    Graphics2D iGraph = (Graphics2D) image.getGraphics();
    iGraph.setPaint (Color.WHITE);
    iGraph.fillRect (0, 0, image.getWidth(), image.getHeight());

    // Get transparent black stamp to cover the first one
    image2 = DefaultProjectionFactory.getRawProjectionImage(image2, Color.BLACK);

    draw(image1, maxDimension, iGraph);
    draw(image2, maxDimension, iGraph);

//    DEBUG
//    JFrame frame = new JFrame();
//    frame.getContentPane().add(new JLabel(new ImageIcon(image)));
//    frame.pack();
//    frame.setVisible(true);

    return new Stamp(image);
  }

  private BufferedImage transparent(BufferedImage image2) {
    return null;
  }

  private void draw(BufferedImage image1, int maxDimension, Graphics2D iGraph) {
    AffineTransform transform = new AffineTransform();
    double scale = Math.max(Math.random(), 0.8);

    int x = (int) (maxDimension / 2 - (image1.getWidth() * scale) / 2);
    int y = (int) (maxDimension / 2 - (image1.getHeight() * scale) / 2);

    //maxDimension = (int) (maxDimension * scale);

    transform.setToTranslation(x, y);
    transform.scale(scale, scale);
    transform.rotate(Math.toRadians(Math.random() * 360), image1.getWidth() / 2d, image1.getHeight() / 2d);


    iGraph.drawImage(image1, transform, null);
  }


}
