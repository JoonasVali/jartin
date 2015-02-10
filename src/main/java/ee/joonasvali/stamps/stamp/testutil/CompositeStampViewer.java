package ee.joonasvali.stamps.stamp.testutil;

import ee.joonasvali.stamps.stamp.CompositeStampCreationException;
import ee.joonasvali.stamps.stamp.RandomIntersectionComposerStrategy;
import ee.joonasvali.stamps.stamp.Stamp;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author Joonas Vali
 */
public class CompositeStampViewer {
  public static void main(String[] args) throws CompositeStampCreationException {
    RandomIntersectionComposerStrategy strategy = new RandomIntersectionComposerStrategy(1);
    Stamp s1 = Stamp.getInstance(new File("K:\\Progemine\\Java\\stamps\\src\\main\\resources\\stamps\\steampunk\\II.png"));
    Stamp s2 = Stamp.getInstance(new File("K:\\Progemine\\Java\\stamps\\src\\main\\resources\\stamps\\grunge\\w4.png"));
    Stamp s3 = strategy.create(s1, s2);
    BufferedImage img = s3.getImg();
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    frame.getContentPane().add(panel);
    panel.add(new JLabel(new ImageIcon(img)));
    frame.pack();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
