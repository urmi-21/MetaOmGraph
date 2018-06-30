package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class ThinDottedBorder implements Border {
    private Color myColor;

    public ThinDottedBorder(Color color) {
        myColor = color;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setPaint(myColor);
        BasicStroke stroke = new BasicStroke(0.1F, 0,
                0, 2.0F, new float[]{1.0F, 1.0F}, 0.0F);
        g2d.setStroke(stroke);
        int myWidth = width;
        if ((c instanceof JLabel)) {
            JLabel label = (JLabel) c;

            myWidth = label.getPreferredSize().width;
        }
        g2d.drawRect(x, y, myWidth - 1, height - 1);
    }
}
