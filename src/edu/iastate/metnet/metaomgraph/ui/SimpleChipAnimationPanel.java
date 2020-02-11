package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class SimpleChipAnimationPanel
        extends JPanel {
    public SimpleChipAnimationPanel() {
    }

    @Override
	protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        for (int x = 0; x < getWidth(); x += 6) {
            for (int y = 0; y < getHeight(); y += 6) {
                double colorValue = Math.random();
                int green;
                int red;
                if (colorValue <= 0.5D) {
                    red = 255;
                    green = (int) (colorValue / 0.5D * 255.0D);
                } else {
                    red = (int) ((1.0D - colorValue) / 0.5D * 255.0D);
                    green = 255;
                }
                g2d.setColor(new Color(red, green, 0));
                g2d.fillOval(x + 2, y + 2, 4, 4);
            }
        }
    }
}
