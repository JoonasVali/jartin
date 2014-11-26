package ee.joonasvali.stamps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {
  static Stamps stamps = new Stamps(new File(Main.class.getResource("/stamps").getFile()));
  public static void main(String[] args) {
    BufferedImage canvas = init(stamps);
    JFrame frame = new JFrame("LOL");
    frame.getContentPane().add(new JLabel(new ImageIcon(canvas)));
    frame.pack();
    frame.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) { }

      @Override
      public void keyPressed(KeyEvent e) { }

      @Override
      public void keyReleased(KeyEvent e) {
        if(KeyEvent.VK_SPACE == e.getKeyCode()) {
          Stamp.clearCache();
          BufferedImage img = init(stamps);
          frame.getContentPane().removeAll();
          frame.getContentPane().add(new JLabel(new ImageIcon(img)));
          frame.pack();
        }
      }
    });
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  }

  private static BufferedImage init(Stamps stamps) {
    int x = 1300;
    int y = 600;
    CompositeStamps compositeStamps = new CompositeStamps(stamps, new RandomComposerStrategy(5));
    StampPallette stampPallette = new StampPallette(compositeStamps, 8);
    RandomQuery<Stamp> ran = new RandomQuery<>();
    RandomQuery<Color> ranColor = new RandomQuery<>();
    Pallette pallette = new Pallette(3);

    ProjectionGenerator gen = new ProjectionGenerator(x, y, stampPallette, pallette);

    Painting painting = new Painting(x, y);
    JFrame frame = new JFrame("LOL");

    int projections = (int) (x * y / 350d);
    for (int i = 0; i < projections; i++) {
      painting.addProjection(gen.generate());
    }

    BufferedImage canvas = painting.getImage();
    return canvas;

  }
}
