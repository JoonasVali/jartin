package ee.joonasvali.stamps.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {

  public static final String SAVE_PATH = "C:\\Users\\Joss\\Desktop\\art\\big\\";
  JFrame frame;
  PaintingUI ui = new PaintingUI();

  public static void main(String[] args) throws InvocationTargetException, InterruptedException {
    SwingUtilities.invokeAndWait(new Runnable() {
      @Override
      public void run() {
        new Main().run();
      }
    });

  }

  private void run() {
    frame = new JFrame("(C) Jartin 1.0.alpha by Joonas Vali 2014");

    JPanel panel = new JPanel(new BorderLayout());
    frame.getContentPane().add(panel);
    JScrollPane scrollPane = new JScrollPane(ui, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    HandScrollListener scrollListener = new HandScrollListener(ui);
    scrollPane.getViewport().addMouseMotionListener(scrollListener);
    scrollPane.getViewport().addMouseListener(scrollListener);

    panel.add(scrollPane, BorderLayout.CENTER);
    frame.setFocusable(true);
    frame.setAutoRequestFocus(true);



    JPanel controlPanel = new JPanel(new FlowLayout());
    JCheckBox box1 = new JCheckBox("Reuse color", false);
    JCheckBox box2 = new JCheckBox("Reuse brushes", false);

    box1.addActionListener(getColorActionListener(box1));
    box2.addActionListener(getStampsActionListener(box2));

    controlPanel.add(box1);
    controlPanel.add(new JSeparator(JSeparator.VERTICAL));
    controlPanel.add(box2);

    JButton generate = new JButton("Generate");
    generate.addActionListener(s -> {
      ui.onReinit();
      scrollPane.revalidate();
    });
    controlPanel.add(generate);

    JButton save = new JButton("Save");
    save.addActionListener(s -> save());
    controlPanel.add(save);


    panel.add(controlPanel, BorderLayout.NORTH);
    frame.pack();
    frame.setSize(new Dimension(800, 600));
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  private void save() {
    try {
      BufferedImage bi = ui.getLastImage();
      String name = System.currentTimeMillis() + ".png";
      File outputfile = new File(SAVE_PATH + name);
      ImageIO.write(bi, "png", outputfile);
      JOptionPane.showMessageDialog(frame, "File saved to " + outputfile.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ActionListener getStampsActionListener(JCheckBox box2) {
    return s -> ui.setRetainStamps(box2.isSelected());
  }

  private ActionListener getColorActionListener(JCheckBox box1) {
    return s -> ui.setRetainColors(box1.isSelected());
  }

}
