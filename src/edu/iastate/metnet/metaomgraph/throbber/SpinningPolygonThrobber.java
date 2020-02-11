package edu.iastate.metnet.metaomgraph.throbber;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;


public class SpinningPolygonThrobber
        extends Throbber {
    private Polygon poly;
    private int radius;
    private double rotation;
    private int sides;

    public SpinningPolygonThrobber() {
        radius = 20;
        sides = 5;
        rotation = 0.0D;
        poly = regularPolygon(25, 25, radius, sides, 0.0D);
    }

    @Override
	public int getThrobberWidth() {
        return 50;
    }

    @Override
	public int getThrobberHeight() {
        return 50;
    }

    private void spin() {
        rotation -= 0.010471975511965976D;
        if (rotation < -6.283185307179586D) {
        }

        rotation += 6.283185307179586D;
        poly = regularPolygon(25, 25, radius, sides, rotation);
    }

    public static Point findNewPoint(int x1, int y1, int length, double angle) {
        int dx = (int) (length * Math.sin(angle));
        int dy = (int) (length * Math.cos(angle));
        int x2 = x1 + dx;
        int y2 = y1 + dy;
        Point result = new Point(x2, y2);
        return result;
    }


    public static Polygon regularPolygon(int xcenter, int ycenter, int radius, int sides, double rotation) {
        int[] xpoints = new int[sides];
        int[] ypoints = new int[sides];
        double delta = 6.283185307179586D / sides;
        for (int i = 0; i < sides; i++) {
            double angle = delta * i + 1.5707963267948966D * rotation;
            xpoints[i] = ((int) (radius * Math.cos(angle) + 0.5D) + xcenter);

            ypoints[i] = (ycenter - (int) (radius * Math.sin(angle) + 0.5D));
        }

        return new Polygon(xpoints, ypoints, sides);
    }


    @Override
	protected void drawThrobber(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getThrobberWidth(), getThrobberHeight());
        spin();
        g2d.setColor(Color.RED);
        g2d.drawPolygon(poly);
    }


    public static void main(String[] args) {
        JFrame f = new JFrame("test");
        final Throbber throbber = new SpinningPolygonThrobber();
        throbber.setDelay(100);
        System.out.println(throbber.getDelay());
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
