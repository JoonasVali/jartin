package ee.joonasvali.stamps;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {

  static PaintingUI ui = new PaintingUI();
  public static void main(String[] args) {

    JFrame frame = new JFrame("Art Generator (C) Joonas Vali 2014");
    frame.getContentPane().add(ui);
    frame.pack();
    frame.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) { }

      @Override
      public void keyPressed(KeyEvent e) { }

      @Override
      public void keyReleased(KeyEvent e) {
        if(KeyEvent.VK_SPACE == e.getKeyCode()) {
          ui.onReinit();
          frame.pack();
        }
      }
    });
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

}
