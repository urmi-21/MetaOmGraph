package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class WelcomeHeader extends javax.swing.JComponent {
    String text;
    Image icon;

    public WelcomeHeader(String text, Image icon) {
        this.text = text;
        this.icon = icon;
        setFont(new JLabel().getFont().deriveFont(18.0F));
    }

    public Dimension getPreferredSize() {
        int width = SwingUtilities.computeStringWidth(this.getFontMetrics(this
                .getFont()), text)
                + icon.getWidth(null) + this.getInsets().left + this.getInsets().right + 50;
        return new Dimension(width, 50);
    }

    protected void paintComponent(Graphics g) {
        GradientPaint gradient = new GradientPaint(0.0F, 0.0F, Color.WHITE, 0.0F,
                getHeight(), new Color(255, 255, 255, 0));


        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.drawImage(icon, 10, getHeight() / 2 - icon.getHeight(null) / 2,
                null);
        g2d.setPaint(Color.BLACK);
        g2d.drawString(text, 15 + icon.getWidth(null), getHeight() / 2 +
                g2d.getFontMetrics().getAscent() / 2);
    }
}
