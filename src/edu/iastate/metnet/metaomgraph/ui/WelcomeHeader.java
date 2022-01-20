package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.*;
import java.awt.*;

public class WelcomeHeader extends Header {

    GradientPaint gradientBlue;
    GradientPaint gradientGreen;

    public WelcomeHeader(String text, Image icon) {
        super(text);
        this.setIcon(new ImageIcon(icon));
    }

    protected void paintGradient(Graphics2D g2d) {
        if (gradientBlue == null) {
            Color TRANSPARENT = new Color(255, 255, 255, 0);
            gradientBlue = new GradientPaint(
                    0, 0, new Color(172, 182, 229, 33),
                    0, getHeight(), new Color(172, 182, 229, 166)
            );

            gradientGreen = new GradientPaint(
                    0, 0, new Color(116, 235, 213),
                    getWidth(), getHeight(), TRANSPARENT
            );
        }

        int yPadding = 5;
        int realHeight = getHeight() - yPadding * 2;

        /* draw gradients */
        g2d.setPaint(gradientBlue);
        g2d.fillRect(0, yPadding, getWidth(), realHeight);
        g2d.setPaint(gradientGreen);
        g2d.fillRect(0, yPadding, getWidth(), realHeight);

        /* draw borders */
        g2d.setColor(new Color(0,0,0,30));
        g2d.fillRect(0, yPadding, getWidth(), 1);
        g2d.fillRect(0, getHeight() - yPadding - 2, getWidth(), 2);

    }


    @Override
    protected void paintComponent(Graphics g) {


        Graphics2D g2d = (Graphics2D) g;

        this.paintGradient(g2d);
        g2d.setPaint(Color.BLACK);

        super.paintComponent(g2d);
    }
}
