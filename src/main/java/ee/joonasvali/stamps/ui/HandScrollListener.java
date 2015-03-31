/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HandScrollListener extends MouseAdapter {
  private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
  private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
  private final Point pp = new Point();
  private final JPanel image;

  public HandScrollListener(JPanel image) {
    this.image = image;
  }

  public void mouseDragged(final MouseEvent e) {
    JViewport vport = (JViewport) e.getSource();
    Point cp = e.getPoint();
    Point vp = vport.getViewPosition();
    vp.translate(pp.x - cp.x, pp.y - cp.y);
    image.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
    pp.setLocation(cp);
  }

  public void mousePressed(MouseEvent e) {
    image.setCursor(hndCursor);
    pp.setLocation(e.getPoint());
  }

  public void mouseReleased(MouseEvent e) {
    image.setCursor(defCursor);
    image.repaint();
  }
}