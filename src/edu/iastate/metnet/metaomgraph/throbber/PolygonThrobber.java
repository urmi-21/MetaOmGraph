package edu.iastate.metnet.metaomgraph.throbber;

import edu.iastate.metnet.metaomgraph.ui.PolygonPanel.BouncingPolygon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

public class PolygonThrobber extends Throbber {
    BouncingPolygon poly;

    public PolygonThrobber() {
        poly = new BouncingPolygon(3, 0, 0, getThrobberWidth(), getThrobberHeight());
    }

    @Override
	public int getThrobberWidth() {
        return 32;
    }

    @Override
	public int getThrobberHeight() {
        return 32;
    }

    @Override
	protected void drawThrobber(Graphics2D g2d) {
        if (poly == null) {
            poly = new BouncingPolygon(3, 0, 0, getThrobberWidth(), getThrobberHeight());
        }
        poly.move();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(getBackground());
        g2d.fillRect(0, 0, getThrobberWidth(), getThrobberHeight());
        g2d.setPaint(poly.getColor());
        g2d.fillPolygon(poly);
        g2d.setPaint(Color.BLACK);
        g2d.drawPolygon(poly);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("test");
        final Throbber throbber = new PolygonThrobber();
        f.getContentPane().add(throbber);
        f.pack();
        f.setDefaultCloseOperation(3);
        f.getContentPane().addMouseListener(new MouseListener() {
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
