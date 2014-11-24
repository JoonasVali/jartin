package ee.joonasvali.stamps;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class DefaultProjection implements Projection {

  private BufferedImage img;
  private int x;
  private int y;
  private int rotation;

  public DefaultProjection(BufferedImage img) {
    this.img = img;
  }

  public BufferedImage getImg() {
    return img;
  }

  public void setImg(BufferedImage img) {
    this.img = img;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getRotation() {
    return rotation;
  }

  public void setRotation(int rotation) {
    this.rotation = rotation;
  }

  public void paintTo(BufferedImage canvas) {
    Graphics2D g1 = (Graphics2D)canvas.getGraphics();
    Graphics2D g2 = (Graphics2D)img.getGraphics();
    AffineTransform at = new AffineTransform();
    at.setToTranslation(x, y);
    at.rotate(Math.toRadians(rotation), img.getWidth()/2, img.getHeight()/2);
    g1.drawImage(img, at, null);
  }
}

