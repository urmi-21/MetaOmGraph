package edu.iastate.metnet.metaomgraph.throbber;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;

public class GlowyChipThrobber extends Throbber {
    public GlowyChipThrobber() {
    }

    private double step = -0.1D;

    private double colorValue = 1.0D;

    @Override
	public int getThrobberWidth() {
        return 8;
    }

    @Override
	public int getThrobberHeight() {
        return 8;
    }

    @Override
	protected void drawThrobber(Graphics2D g2d) {
        g2d.setPaint(getBackground());
        if (!isAnimating()) {
            g2d.setPaint(Color.GRAY);
            g2d.fillOval(0, 0, getThrobberWidth(), getThrobberHeight());
            return;
        }
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
        g2d.fillOval(0, 0, getThrobberWidth(), getThrobberHeight());
        colorValue += step;
        if (colorValue <= 0.0D) {
            colorValue = 0.0D;
            step = (-step);
        }
        if (colorValue >= 1.0D) {
            colorValue = 1.0D;
            step = (-step);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("test");
        final Throbber throbber = new GlowyChipThrobber();
        f.getContentPane().add(throbber);
        f.pack();
        f.setDefaultCloseOperation(3);
        f.getContentPane().addMouseListener(new java.awt.event.MouseListener() {
            @Override
			public void mouseClicked(MouseEvent e) {
                if (isAnimating()) {
                    throbber.stop();
                } else {
                    throbber.start();
                }
            }


            @Override
			public void mousePressed(MouseEvent e) {
            }


            @Override
			public void mouseReleased(MouseEvent e) {
            }


            @Override
			public void mouseEntered(MouseEvent e) {
            }


            @Override
			public void mouseExited(MouseEvent e) {
            }
        });
        f.setVisible(true);
    }
}
