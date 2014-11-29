package ee.joonasvali.stamps;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {
  static GroupedStamps stampPool = new GroupedStamps(new File(Main.class.getResource("/stamps").getFile()));
  public static void main(String[] args) {

    Stamps stamps = generateNewStamps();
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
          clearCaches();
          BufferedImage img = init(generateNewStamps());
          frame.getContentPane().removeAll();
          frame.getContentPane().add(new JLabel(new ImageIcon(img)));
          frame.pack();
        }
      }
    });
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  private static void clearCaches() {
    Stamp.clearCache();
    stampPool.clearCaches();
  }

  private static Stamps generateNewStamps() {
    return stampPool.getStamps(5, 3, new RandomQuery<>(), new RandomQuery<>(), false);
  }

  private static BufferedImage init(Stamps stamps) {
    //int x = 1300;
    //int y = 600;
    int x = 1900;
    int y = 1000;
    CompositeStamps compositeStamps = new CompositeStamps(stamps, new RandomComposerStrategy(5));
    Pallette pallette = new Pallette(3);

    ProjectionGenerator gen = new ProjectionGenerator(x, y, stamps, pallette);

    Painting painting = new Painting(x, y, pallette);

    int projections = (int) (x * y / 500d);

    System.out.println(projections);
    for (int i = 0; i < projections; i++) {
      painting.addProjection(gen.generate());
    }

    BufferedImage canvas = painting.getImage();
    return canvas;

  }
}
