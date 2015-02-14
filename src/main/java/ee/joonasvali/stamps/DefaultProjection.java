package ee.joonasvali.stamps;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class DefaultProjection implements Projection {

  private volatile BufferedImage img;
  private volatile int x;
  private volatile int y;
  private volatile double scale;
  private volatile int rotation;

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

  public double getScale() {
    return scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
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
    g1.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    AffineTransform at = new AffineTransform();
    at.setToTranslation(x, y);
    at.scale(scale, scale);
    at.rotate(Math.toRadians(rotation), img.getWidth()/2, img.getHeight()/2);
    g1.drawImage(img, at, null);
  }
}

