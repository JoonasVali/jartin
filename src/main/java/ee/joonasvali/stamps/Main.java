package ee.joonasvali.stamps;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {
  public static void main(String[] args) {

    Stamps stamps = new Stamps(new File(Main.class.getResource("/stamps").getFile()));
    RandomQuery<Stamp> ran = new RandomQuery<>();

    DefaultProjection img1 = (DefaultProjection) stamps.getStamp(ran).getProjection(new Color(100, 150, 10));
    DefaultProjection img2 = (DefaultProjection) stamps.getStamp(ran).getProjection(new Color(120, 170, 50));
    DefaultProjection img3 = (DefaultProjection) stamps.getStamp(ran).getProjection(new Color(100, 200, 20));
    DefaultProjection img4 = (DefaultProjection) stamps.getStamp(ran).getProjection(new Color(100, 150, 10));

    Painting painting = new Painting(500, 500);
    JFrame frame = new JFrame("LOL");



    img1.setRotation(200);
    img2.setRotation(210);
    img3.setRotation(230);
    img4.setRotation(300);

    img1.setX(150);
    img2.setX(160);
    img3.setX(170);
    img4.setX(180);

    img1.setY(150);
    img2.setY(160);
    img3.setY(170);
    img4.setY(180);

    painting.addProjection(img1);
    painting.addProjection(img2);
    painting.addProjection(img3);
    painting.addProjection(img4);

    BufferedImage canvas = painting.getImage();

    frame.getContentPane().add(new JLabel(new ImageIcon(canvas)));
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  }
}
