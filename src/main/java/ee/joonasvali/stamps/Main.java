package ee.joonasvali.stamps;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {
  public static void main(String[] args) {

    Stamps stamps = new Stamps(new File(Main.class.getResource("/stamps").getFile()));
    RandomQuery<Stamp> ran = new RandomQuery<>();
    RandomQuery<Color> ranColor = new RandomQuery<>();
    Pallette pallette = new Pallette(3);

    DefaultProjection img1 = (DefaultProjection) stamps.getStamp(ran).getProjection(pallette.getColor(ranColor));
    DefaultProjection img2 = (DefaultProjection) stamps.getStamp(ran).getProjection(pallette.getColor(ranColor));
    DefaultProjection img3 = (DefaultProjection) stamps.getStamp(ran).getProjection(pallette.getColor(ranColor));
    DefaultProjection img4 = (DefaultProjection) stamps.getStamp(ran).getProjection(pallette.getColor(ranColor));

    Painting painting = new Painting(500, 500);
    JFrame frame = new JFrame("LOL");



    img1.setRotation((int) (Math.random() * 360));
    img2.setRotation((int) (Math.random() * 360));
    img3.setRotation((int) (Math.random() * 360));
    img4.setRotation((int) (Math.random() * 360));

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
